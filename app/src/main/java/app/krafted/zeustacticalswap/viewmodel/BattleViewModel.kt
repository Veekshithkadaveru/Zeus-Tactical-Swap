package app.krafted.zeustacticalswap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.krafted.zeustacticalswap.data.db.BossProgressRepository
import app.krafted.zeustacticalswap.game.BossAI
import app.krafted.zeustacticalswap.game.BossAttackResult
import app.krafted.zeustacticalswap.game.BossId
import app.krafted.zeustacticalswap.game.BossSpecialResult
import app.krafted.zeustacticalswap.game.BossState
import app.krafted.zeustacticalswap.game.CascadeProcessor
import app.krafted.zeustacticalswap.game.CombatResolver
import app.krafted.zeustacticalswap.game.CombatResult
import app.krafted.zeustacticalswap.game.GridEngine
import app.krafted.zeustacticalswap.game.Match
import app.krafted.zeustacticalswap.game.MatchDetector
import app.krafted.zeustacticalswap.game.PlayerState
import app.krafted.zeustacticalswap.game.Symbol
import app.krafted.zeustacticalswap.game.TileState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class TurnPhase {
    PLAYER_INPUT,
    RESOLVING_MATCHES,
    CASCADE_CHECK,
    APPLYING_COMBAT,
    BOSS_ATTACK,
    BOSS_SPECIAL,
    STATUS_TICK,
    CHECK_VICTORY,
    CHECK_DEFEAT
}

data class BattleUiState(
    val grid: List<List<TileState>> = emptyList(),
    val selectedTile: Pair<Int, Int>? = null,
    val swappingPair: Pair<Pair<Int, Int>, Pair<Int, Int>>? = null,
    val matchedCells: Set<Pair<Int, Int>> = emptySet(),
    val newCells: Set<Pair<Int, Int>> = emptySet(),
    val isInvalidSwap: Boolean = false,
    val player: PlayerState = PlayerState(),
    val boss: BossState = BossState(),
    val lastActionText: String = "",
    val lastActionSymbol: Symbol? = null,
    val cascadeCount: Int = 0,
    val isPlayerTurn: Boolean = true,
    val isBossDefeated: Boolean = false,
    val isPlayerDefeated: Boolean = false,
    val currentBossIndex: Int = 0,
    val defeatedBosses: List<Int> = emptyList(),
    val bestClearTimes: Map<BossId, String> = emptyMap(),
    val phase: TurnPhase = TurnPhase.PLAYER_INPUT
)

class BattleViewModel(private val repository: BossProgressRepository) : ViewModel() {

    // WARNING: Changing the order of elements in the BossId enum will change
    // the sequence of stages because bossOrder maps indices to stages.
    private val bossOrder = BossId.values().toList()
    private var battleStartTime: Long = 0L

    private val _uiState = MutableStateFlow(newBattleState(0))
    val uiState: StateFlow<BattleUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allRecords.collect { records ->
                val defeated = records.filter { it.defeated }.map { record ->
                    BossId.values().indexOfFirst { it.name == record.bossId }
                }.filter { it != -1 }

                val times = records.mapNotNull { record ->
                    val bossId = BossId.values().firstOrNull { it.name == record.bossId }
                        ?: return@mapNotNull null
                    val timeStr = record.bestClearTimeMillis?.let { millis ->
                        val totalSecs = millis / 1000
                        val mins = totalSecs / 60
                        val secs = totalSecs % 60
                        String.format("%02d:%02d", mins, secs)
                    } ?: "--:--"
                    bossId to timeStr
                }.toMap()

                _uiState.update {
                    it.copy(
                        defeatedBosses = defeated,
                        bestClearTimes = times
                    )
                }
            }
        }
    }

    fun startBattle() {
        battleStartTime = System.currentTimeMillis()
    }

    private fun newBattleState(bossIndex: Int): BattleUiState {
        val index = bossIndex.coerceIn(0, bossOrder.size - 1)
        val boss = BossState.forBoss(bossOrder[index])
        return BattleUiState(
            grid = GridEngine.makeGrid(),
            boss = boss,
            currentBossIndex = index
        )
    }

    fun loadBoss(bossIndex: Int) {
        val index = bossIndex.coerceIn(0, bossOrder.size - 1)
        val boss = BossState.forBoss(bossOrder[index])
        _uiState.update {
            it.copy(
                grid = GridEngine.makeGrid(),
                player = PlayerState(maxHp = it.player.maxHp, currentHp = it.player.maxHp),
                boss = boss,
                currentBossIndex = index,
                isBossDefeated = false,
                isPlayerDefeated = false,
                phase = TurnPhase.PLAYER_INPUT,
                isPlayerTurn = true,
                lastActionText = "Tap two adjacent tiles to swap",
                lastActionSymbol = null,
                cascadeCount = 0,
                matchedCells = emptySet(),
                newCells = emptySet(),
                swappingPair = null
            )
        }
    }

    fun onTileTapped(row: Int, col: Int) {
        val state = _uiState.value
        if (state.phase != TurnPhase.PLAYER_INPUT || !state.isPlayerTurn) return
        if (state.boss.isDefeated || !state.player.isAlive) return
        if (row !in state.grid.indices || col !in state.grid[row].indices) return
        if (state.grid[row][col].symbol == Symbol.SKULL) return

        val tapped = Pair(row, col)
        val selected = state.selectedTile

        if (selected == null) {
            _uiState.update { it.copy(grid = markSelected(it.grid, tapped), selectedTile = tapped) }
            return
        }

        if (selected == tapped) {
            _uiState.update { it.copy(grid = clearSelection(it.grid), selectedTile = null) }
            return
        }

        if (GridEngine.isAdjacent(selected, tapped)) {
            attemptSwap(selected, tapped)
        } else {
            _uiState.update {
                it.copy(grid = markSelected(clearSelection(it.grid), tapped), selectedTile = tapped)
            }
        }
    }

    private fun attemptSwap(a: Pair<Int, Int>, b: Pair<Int, Int>) {
        val cleared = clearSelection(_uiState.value.grid)
        val swapped = GridEngine.swap(cleared, a, b)
        val matches = MatchDetector.findAllMatches(swapped)

        if (matches.isEmpty()) {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        grid = swapped,
                        selectedTile = null,
                        isInvalidSwap = true,
                        swappingPair = Pair(a, b)
                    )
                }
                delay(300L)
                _uiState.update {
                    it.copy(grid = cleared, isInvalidSwap = false, swappingPair = null)
                }
            }
            return
        }

        _uiState.update {
            it.copy(
                grid = swapped,
                selectedTile = null,
                isPlayerTurn = false,
                cascadeCount = 0,
                phase = TurnPhase.RESOLVING_MATCHES
            )
        }
        runTurn(swapped)
    }

    private fun runTurn(swappedGrid: List<List<TileState>>) {
        viewModelScope.launch {
            try {
                val cascade = CascadeProcessor.process(swappedGrid)

                var player = _uiState.value.player
                var boss = _uiState.value.boss
                val actionSummary = StringBuilder()

                cascade.steps.forEachIndexed { index, step ->
                    val matchedCells = step.matches.flatMap { it.cells }.toSet()
                    val lastSym = step.matches.lastOrNull()?.symbol
                    _uiState.update {
                        it.copy(
                            matchedCells = matchedCells,
                            cascadeCount = index,
                            phase = TurnPhase.RESOLVING_MATCHES,
                            lastActionText = describeMatches(step.matches, step.cascadeMultiplier),
                            lastActionSymbol = lastSym
                        )
                    }
                    delay(400L)

                    val results = CombatResolver.resolveAllMatches(
                        step.matches,
                        criticalActive = player.criticalActive,
                        cascadeMultiplier = step.cascadeMultiplier
                    )
                    val applied = CombatResolver.applyCombatResults(results, player, boss)
                    player = applied.first
                    boss = applied.second
                    if (actionSummary.isNotEmpty()) actionSummary.append(" → ")
                    actionSummary.append(summarizeResults(results))

                    _uiState.update {
                        it.copy(
                            grid = step.gridAfter,
                            matchedCells = emptySet(),
                            newCells = step.newCells,
                            player = player,
                            boss = boss,
                            cascadeCount = index,
                            phase = TurnPhase.CASCADE_CHECK,
                            lastActionSymbol = lastSym
                        )
                    }
                    delay(300L)

                    if (boss.isDefeated) {
                        _uiState.update {
                            it.copy(
                                grid = cascade.finalGrid,
                                newCells = emptySet(),
                                phase = TurnPhase.APPLYING_COMBAT,
                                lastActionText = if (actionSummary.isNotEmpty()) actionSummary.toString() else it.lastActionText,
                                lastActionSymbol = lastSym
                            )
                        }
                        delay(600L)
                        onBossDefeated(player, boss)
                        return@launch
                    }
                }

                val finalSym = cascade.steps.flatMap { it.matches }.lastOrNull()?.symbol
                _uiState.update {
                    it.copy(
                        grid = cascade.finalGrid,
                        newCells = emptySet(),
                        phase = TurnPhase.APPLYING_COMBAT,
                        lastActionText = if (actionSummary.isNotEmpty()) actionSummary.toString() else it.lastActionText,
                        lastActionSymbol = finalSym
                    )
                }
                delay(600L)

                if (boss.isDefeated) {
                    onBossDefeated(player, boss)
                    return@launch
                }

                _uiState.update { it.copy(phase = TurnPhase.BOSS_ATTACK) }
                val attack = BossAI.calculateAttack(boss, player)
                player = BossAI.applyAttack(attack, player)
                _uiState.update {
                    it.copy(player = player, lastActionText = describeAttack(attack), lastActionSymbol = null)
                }
                delay(400L)

                _uiState.update { it.copy(phase = TurnPhase.BOSS_SPECIAL) }
                val (specialGrid, special) = BossAI.triggerSpecial(boss, _uiState.value.grid)
                if (special !is BossSpecialResult.None) {
                    _uiState.update {
                        it.copy(grid = specialGrid, lastActionText = describeSpecial(special), lastActionSymbol = null)
                    }
                    delay(300L)

                    if (special is BossSpecialResult.Cyclone) {
                        val specialCascade = CascadeProcessor.process(specialGrid)
                        if (specialCascade.steps.isNotEmpty()) {
                            val actionSum = StringBuilder()
                            specialCascade.steps.forEachIndexed { index, step ->
                                val matchedCells = step.matches.flatMap { it.cells }.toSet()
                                val lastSym = step.matches.lastOrNull()?.symbol
                                _uiState.update {
                                    it.copy(
                                        matchedCells = matchedCells,
                                        cascadeCount = index,
                                        phase = TurnPhase.RESOLVING_MATCHES,
                                        lastActionText = describeMatches(
                                            step.matches,
                                            step.cascadeMultiplier
                                        ),
                                        lastActionSymbol = lastSym
                                    )
                                }
                                delay(400L)

                                val results = CombatResolver.resolveAllMatches(
                                    step.matches,
                                    criticalActive = player.criticalActive,
                                    cascadeMultiplier = step.cascadeMultiplier
                                )
                                val applied = CombatResolver.applyCombatResults(results, player, boss)
                                player = applied.first
                                boss = applied.second
                                if (actionSum.isNotEmpty()) actionSum.append(" → ")
                                actionSum.append(summarizeResults(results))

                                _uiState.update {
                                    it.copy(
                                        grid = step.gridAfter,
                                        matchedCells = emptySet(),
                                        newCells = step.newCells,
                                        player = player,
                                        boss = boss,
                                        cascadeCount = index,
                                        phase = TurnPhase.CASCADE_CHECK,
                                        lastActionSymbol = lastSym
                                    )
                                }
                                delay(300L)

                                if (boss.isDefeated) {
                                    _uiState.update {
                                        it.copy(
                                            grid = specialCascade.finalGrid,
                                            newCells = emptySet(),
                                            player = player,
                                            boss = boss,
                                            phase = TurnPhase.APPLYING_COMBAT,
                                            lastActionText = if (actionSum.isNotEmpty()) actionSum.toString() else it.lastActionText,
                                            lastActionSymbol = lastSym
                                        )
                                    }
                                    delay(600L)
                                    onBossDefeated(player, boss)
                                    return@launch
                                }
                            }

                            val finalSpecialSym = specialCascade.steps.flatMap { it.matches }.lastOrNull()?.symbol
                            _uiState.update {
                                it.copy(
                                    grid = specialCascade.finalGrid,
                                    newCells = emptySet(),
                                    player = player,
                                    boss = boss,
                                    phase = TurnPhase.APPLYING_COMBAT,
                                    lastActionText = if (actionSum.isNotEmpty()) actionSum.toString() else it.lastActionText,
                                    lastActionSymbol = finalSpecialSym
                                )
                            }
                            delay(600L)
                        }
                    }
                } else {
                    delay(300L)
                }

                _uiState.update { it.copy(phase = TurnPhase.STATUS_TICK) }
                val (tickedBoss, poisonDamage) = boss.tickStatusEffects()
                boss = tickedBoss.advanceTurn()
                player = player.onTurnEnd()
                _uiState.update {
                    it.copy(
                        player = player,
                        boss = boss,
                        lastActionText = if (poisonDamage > 0) "Poison deals $poisonDamage" else it.lastActionText,
                        lastActionSymbol = if (poisonDamage > 0) Symbol.AMPHORA else null
                    )
                }
                delay(300L)

                _uiState.update { it.copy(phase = TurnPhase.CHECK_VICTORY) }
                if (boss.isDefeated) {
                    onBossDefeated(player, boss)
                    return@launch
                }

                _uiState.update { it.copy(phase = TurnPhase.CHECK_DEFEAT) }
                if (!player.isAlive) {
                    _uiState.update {
                        it.copy(
                            isPlayerDefeated = true,
                            isPlayerTurn = false,
                            phase = TurnPhase.CHECK_DEFEAT
                        )
                    }
                    return@launch
                }

                _uiState.update {
                    it.copy(isPlayerTurn = true, phase = TurnPhase.PLAYER_INPUT)
                }
            } catch (e: Exception) {
                android.util.Log.e("BattleViewModel", "Error in runTurn machine", e)
                _uiState.update {
                    it.copy(
                        isPlayerTurn = true,
                        phase = TurnPhase.PLAYER_INPUT,
                        lastActionText = "An error occurred. Try again."
                    )
                }
            }
        }
    }

    private fun onBossDefeated(player: PlayerState, boss: BossState) {
        val index = _uiState.value.currentBossIndex
        val elapsed = if (battleStartTime > 0L) System.currentTimeMillis() - battleStartTime else 0L
        viewModelScope.launch {
            repository.markDefeated(boss.id, elapsed)
        }
        _uiState.update {
            it.copy(
                player = player,
                boss = boss,
                isBossDefeated = true,
                isPlayerTurn = false,
                phase = TurnPhase.CHECK_VICTORY,
                defeatedBosses = (it.defeatedBosses + index).distinct(),
                lastActionText = "${boss.name} defeated!"
            )
        }
    }

    fun advanceToNextBoss() {
        val state = _uiState.value
        val nextIndex = state.currentBossIndex + 1
        if (nextIndex >= bossOrder.size) return
        val nextBoss = BossState.forBoss(bossOrder[nextIndex])
        _uiState.update {
            it.copy(
                grid = GridEngine.makeGrid(),
                player = it.player.copy(
                    currentHp = it.player.maxHp,
                    shieldHp = 0,
                    chargeCount = 0,
                    criticalActive = false,
                    criticalTurnsLeft = 0
                ),
                boss = nextBoss,
                currentBossIndex = nextIndex,
                isBossDefeated = false,
                isPlayerDefeated = false,
                phase = TurnPhase.PLAYER_INPUT,
                isPlayerTurn = true,
                lastActionText = "Tap two adjacent tiles to swap",
                lastActionSymbol = null,
                cascadeCount = 0,
                matchedCells = emptySet(),
                newCells = emptySet(),
                swappingPair = null
            )
        }
    }

    fun restartCurrentBoss() {
        val state = _uiState.value
        _uiState.update {
            newBattleState(state.currentBossIndex).copy(defeatedBosses = state.defeatedBosses)
        }
    }

    private fun markSelected(
        grid: List<List<TileState>>,
        cell: Pair<Int, Int>
    ): List<List<TileState>> {
        return grid.mapIndexed { r, row ->
            row.mapIndexed { c, tile ->
                tile.copy(isSelected = r == cell.first && c == cell.second)
            }
        }
    }

    private fun clearSelection(grid: List<List<TileState>>): List<List<TileState>> {
        return grid.map { row ->
            row.map { tile ->
                if (tile.isSelected) tile.copy(isSelected = false) else tile
            }
        }
    }

    private fun describeMatches(matches: List<Match>, multiplier: Float): String {
        val labels = matches.joinToString(", ") { "${it.symbol.label} x${it.cells.size}" }
        return if (multiplier > 1.0f) "$labels (${String.format("%.1f", multiplier)}x cascade)" else labels
    }

    private fun summarizeResults(results: List<CombatResult>): String {
        return results.joinToString(", ") { result ->
            when (result) {
                is CombatResult.Attack -> "${result.damage} dmg"
                is CombatResult.Heal -> "+${result.amount} HP"
                is CombatResult.Shield -> "+${result.absorption} shield"
                is CombatResult.Charge -> "+${result.chargesAdded} charge"
                is CombatResult.CriticalStrike -> "Critical ${result.turnsActive}t"
                is CombatResult.Poison -> "Poison ${result.damagePerTurn}/t"
                is CombatResult.Petrify -> "Petrify ${result.turnsRemaining}t"
            }
        }
    }

    private fun describeAttack(attack: BossAttackResult): String {
        return when {
            attack.wasPetrified -> "Boss is petrified — no attack"
            attack.wasEnraged -> "Enraged! ${attack.finalDamage} dmg"
            else -> "Boss hits for ${attack.finalDamage}"
        }
    }

    private fun describeSpecial(special: BossSpecialResult): String {
        return when (special) {
            is BossSpecialResult.Cyclone -> "Cyclone clears row ${special.row}"
            is BossSpecialResult.Corruption -> "Corruption spawns ${special.cells.size} skulls"
            BossSpecialResult.None -> ""
        }
    }

    companion object {
        fun provideFactory(repository: BossProgressRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BattleViewModel(repository) as T
                }
            }
    }
}
