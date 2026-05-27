package app.krafted.zeustacticalswap.game

import kotlin.random.Random

data class BossAttackResult(
    val rawDamage: Int,
    val absorbed: Int,
    val finalDamage: Int,
    val wasPetrified: Boolean,
    val wasEnraged: Boolean
)

sealed class BossSpecialResult {
    object None : BossSpecialResult()
    data class Cyclone(val row: Int) : BossSpecialResult()
    data class Corruption(val cells: List<Pair<Int, Int>>) : BossSpecialResult()
}

object BossAI {

    fun calculateAttack(boss: BossState, player: PlayerState): BossAttackResult {
        if (boss.petrifyTurnsLeft > 0) {
            return BossAttackResult(
                rawDamage = 0,
                absorbed = 0,
                finalDamage = 0,
                wasPetrified = true,
                wasEnraged = false
            )
        }

        val rawDamage = Random.nextInt(boss.minAttack, boss.maxAttack + 1)
        val enraged = boss.isEnraged
        val effectiveDamage = if (enraged) rawDamage * 2 else rawDamage
        val absorbed = minOf(player.shieldHp, effectiveDamage)
        val finalDamage = effectiveDamage - absorbed

        return BossAttackResult(
            rawDamage = rawDamage,
            absorbed = absorbed,
            finalDamage = finalDamage,
            wasPetrified = false,
            wasEnraged = enraged
        )
    }

    fun applyAttack(attackResult: BossAttackResult, player: PlayerState): PlayerState {
        if (attackResult.wasPetrified) return player
        return player.copy(
            shieldHp = player.shieldHp - attackResult.absorbed,
            currentHp = maxOf(0, player.currentHp - attackResult.finalDamage)
        )
    }

    fun triggerSpecial(
        boss: BossState,
        grid: List<List<TileState>>
    ): Pair<List<List<TileState>>, BossSpecialResult> {
        return when (boss.id) {
            BossId.KRONOS -> Pair(grid, BossSpecialResult.None)

            BossId.TYPHON -> {
                if (boss.id.specialInterval > 0 && boss.turnCount > 0 && boss.turnCount % boss.id.specialInterval == 0) {
                    val row = Random.nextInt(grid.size)
                    val cleared = grid.mapIndexed { r, rowTiles ->
                        if (r == row) {
                            rowTiles.map { it.copy(isMatched = true) }
                        } else {
                            rowTiles
                        }
                    }
                    val dropResult = GridEngine.dropTiles(cleared)
                    Pair(dropResult.grid, BossSpecialResult.Cyclone(row))
                } else {
                    Pair(grid, BossSpecialResult.None)
                }
            }

            BossId.HADES -> {
                if (boss.id.specialInterval > 0 && boss.turnCount > 0 && boss.turnCount % boss.id.specialInterval == 0) {
                    val size = grid.size
                    val candidates = mutableListOf<Pair<Int, Int>>()
                    for (r in 0 until size) {
                        for (c in 0 until size) {
                            if (grid[r][c].symbol != Symbol.SKULL) {
                                candidates.add(Pair(r, c))
                            }
                        }
                    }
                    val corruptedCells = if (candidates.isNotEmpty()) {
                        candidates.shuffled().take(minOf(3, candidates.size))
                    } else {
                        emptyList()
                    }
                    val mutableGrid = grid.map { it.toMutableList() }.toMutableList()
                    for ((r, c) in corruptedCells) {
                        mutableGrid[r][c] = GridEngine.makeTile(Symbol.SKULL)
                    }
                    Pair(
                        mutableGrid.map { it.toList() },
                        BossSpecialResult.Corruption(corruptedCells)
                    )
                } else {
                    Pair(grid, BossSpecialResult.None)
                }
            }
        }
    }
}
