package app.krafted.zeustacticalswap.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import app.krafted.zeustacticalswap.game.Symbol
import app.krafted.zeustacticalswap.ui.components.ActionLabel
import app.krafted.zeustacticalswap.ui.components.BossPanel
import app.krafted.zeustacticalswap.ui.components.CombatEffectsOverlay
import app.krafted.zeustacticalswap.ui.components.FlashKind
import app.krafted.zeustacticalswap.ui.components.GameGrid
import app.krafted.zeustacticalswap.ui.components.PlayerPanel
import app.krafted.zeustacticalswap.ui.components.RulesDialog
import app.krafted.zeustacticalswap.ui.theme.Zeus
import app.krafted.zeustacticalswap.viewmodel.BattleViewModel
import app.krafted.zeustacticalswap.viewmodel.TurnPhase
import kotlinx.coroutines.delay



@Composable
fun BattleScreen(
    viewModel: BattleViewModel,
    onQuit: () -> Unit,
    onVictory: () -> Unit,
    onDefeat: () -> Unit,
    onArenaComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var showRules by remember { mutableStateOf(false) }

    if (showRules) {
        RulesDialog(onDismiss = { showRules = false })
    }

    LaunchedEffect(Unit) {
        viewModel.startBattle()
    }

    LaunchedEffect(state.isBossDefeated) {
        if (state.isBossDefeated) {
            delay(1000L)
            if (state.currentBossIndex == 2) onArenaComplete() else onVictory()
        }
    }

    LaunchedEffect(state.isPlayerDefeated) {
        if (state.isPlayerDefeated) {
            delay(1000L)
            onDefeat()
        }
    }

    val lastActionColor = actionColorFor(state.lastActionSymbol, state.phase)
    val flash = flashFor(state.lastActionSymbol, state.phase, state.boss.petrifyTurnsLeft > 0)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Zeus.Night)
    ) {
        Image(
            painter = painterResource(state.boss.backgroundRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (state.boss.isEnraged) 0.7f else 0.55f),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.4f),
                            Zeus.Night.copy(alpha = 0.1f),
                            Zeus.Night.copy(alpha = 0.5f),
                            Zeus.Night.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .heightIn(min = 40.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .border(1.dp, Zeus.Gold.copy(alpha = 0.35f), RoundedCornerShape(100.dp))
                        .clickable(onClick = onQuit),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "← FLEE",
                        style = Zeus.monoLabel(size = 11, color = Zeus.InkDim, tracking = 0.16),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "TURN ${state.boss.turnCount + 1}",
                        style = Zeus.monoLabel(size = 10, color = Zeus.InkMute, tracking = 0.2)
                    )
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.dp, Zeus.Gold.copy(alpha = 0.45f), CircleShape)
                            .clickable { showRules = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "?", color = Zeus.InkDim, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            BossPanel(
                boss = state.boss,
                trialNumber = state.currentBossIndex + 1,
                modifier = Modifier.padding(top = 6.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                contentAlignment = Alignment.Center
            ) {
                if (state.lastActionText.isNotEmpty()) {
                    Text(
                        text = state.lastActionText.uppercase(),
                        color = lastActionColor,
                        fontFamily = Zeus.Display,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 2.6.sp,
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            shadow = Shadow(color = lastActionColor, offset = Offset(0f, 0f), blurRadius = 12f)
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                GameGrid(
                    grid = state.grid,
                    selectedTile = state.selectedTile,
                    invalidSwapCells = state.swappingPair?.let { setOf(it.first, it.second) }
                        ?: emptySet(),
                    onTileTap = { r, c -> viewModel.onTileTapped(r, c) },
                    criticalActive = state.player.criticalActive
                )
            }

            SymbolLegend()

            PlayerPanel(
                player = state.player,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        CombatEffectsOverlay(
            flash = flash,
            cascadeCount = if (state.cascadeCount > 0) state.cascadeCount + 1 else 0
        )

        val showActionLabel = state.phase == TurnPhase.RESOLVING_MATCHES || 
                              state.phase == TurnPhase.CASCADE_CHECK || 
                              state.phase == TurnPhase.APPLYING_COMBAT
        if (showActionLabel && state.lastActionText.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                ActionLabel(text = state.lastActionText, colour = lastActionColor)
            }
        }
    }
}

private fun actionColorFor(symbol: Symbol?, phase: TurnPhase): Color {
    if (symbol != null) return symbol.glowColor
    return when (phase) {
        TurnPhase.BOSS_ATTACK -> Zeus.Crimson
        TurnPhase.BOSS_SPECIAL -> Zeus.GoldHi
        else -> Zeus.InkDim
    }
}

private fun flashFor(symbol: Symbol?, phase: TurnPhase, petrified: Boolean): FlashKind? {
    if (symbol != null) {
        return when (symbol) {
            Symbol.LIGHTNING -> FlashKind.ATTACK
            Symbol.OWL -> FlashKind.HEAL
            Symbol.TRIDENT -> FlashKind.SHIELD
            Symbol.HELMET -> FlashKind.CRIT
            Symbol.LAUREL -> FlashKind.CRIT
            Symbol.AMPHORA -> FlashKind.POISON
            Symbol.MEDUSA -> FlashKind.PETRIFY
            Symbol.SKULL -> null
        }
    }
    return when {
        phase == TurnPhase.BOSS_ATTACK && !petrified -> FlashKind.ATTACK
        else -> null
    }
}

@Composable
private fun SymbolLegend() {
    val symbols = Symbol.values().filter { it != Symbol.SKULL }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        symbols.forEach { sym ->
            Image(
                painter = painterResource(sym.drawableRes),
                contentDescription = sym.label,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

