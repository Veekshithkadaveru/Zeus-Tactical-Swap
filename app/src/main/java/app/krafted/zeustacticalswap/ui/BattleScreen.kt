package app.krafted.zeustacticalswap.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.zeustacticalswap.game.Symbol
import app.krafted.zeustacticalswap.ui.components.ActionLabel
import app.krafted.zeustacticalswap.ui.components.BossPanel
import app.krafted.zeustacticalswap.ui.components.CombatEffectsOverlay
import app.krafted.zeustacticalswap.ui.components.GameGrid
import app.krafted.zeustacticalswap.ui.components.PlayerPanel
import app.krafted.zeustacticalswap.viewmodel.BattleViewModel
import app.krafted.zeustacticalswap.viewmodel.TurnPhase
import kotlinx.coroutines.delay

private val Night = Color(0xFF04050F)
private val Gold = Color(0xFFE7B549)
private val LegendGlow = Color(0xFFE7B549)
private val LegendText = Color(0xFFEDE7D6)

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

    LaunchedEffect(Unit) {
        viewModel.startBattle()
    }

    LaunchedEffect(state.isBossDefeated) {
        if (state.isBossDefeated) {
            delay(1000L)
            if (state.currentBossIndex == 2) {
                onArenaComplete()
            } else {
                onVictory()
            }
        }
    }

    LaunchedEffect(state.isPlayerDefeated) {
        if (state.isPlayerDefeated) {
            delay(1000L)
            onDefeat()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Night)
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
                            Night.copy(alpha = 0.1f),
                            Night.copy(alpha = 0.5f),
                            Night.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 22.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onQuit,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Gold.copy(alpha = 0.35f)
                    ),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB8AE97)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "← FLEE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "TURN ${state.boss.turnCount + 1}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Color(0xFF7A745F),
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            BossPanel(
                boss = state.boss,
                modifier = Modifier.padding(top = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                contentAlignment = Alignment.Center
            ) {
                if (state.lastActionText.isNotEmpty()) {
                    Text(
                        text = state.lastActionText,
                        color = when (state.phase) {
                            TurnPhase.BOSS_ATTACK -> Color(0xFFEF4444)
                            TurnPhase.BOSS_SPECIAL -> Color(0xFFFBBF24)
                            TurnPhase.STATUS_TICK -> Color(0xFF4ADE80)
                            else -> Color(0xFFFFF1F1)
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.3f))
                    .border(1.dp, Gold.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
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

                if (state.cascadeCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .border(1.dp, Gold, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "COMBO ×${state.cascadeCount + 1}",
                            color = Gold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            SymbolLegend()

            PlayerPanel(
                player = state.player,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        CombatEffectsOverlay(
            attackFlash = state.phase == TurnPhase.BOSS_ATTACK && !state.boss.petrifyTurnsLeft.let { it > 0 },
            healFlash = state.phase == TurnPhase.APPLYING_COMBAT && state.lastActionText.contains("HP"),
            cascadeLevel = state.cascadeCount
        )

        AnimatedVisibility(
            visible = state.phase == TurnPhase.RESOLVING_MATCHES && state.lastActionText.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ActionLabel(
                    text = state.lastActionText,
                    colour = if (state.lastActionText.contains("DMG")) Color(0xFFEF4444) else Color(
                        0xFF4ADE80
                    )
                )
            }
        }
    }
}

@Composable
private fun SymbolLegend() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(vertical = 6.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val symbols = listOf(
            Symbol.LIGHTNING,
            Symbol.OWL,
            Symbol.TRIDENT,
            Symbol.HELMET,
            Symbol.LAUREL,
            Symbol.AMPHORA,
            Symbol.MEDUSA
        )
        symbols.forEach { sym ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(sym.drawableRes),
                    contentDescription = sym.label,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = sym.label,
                    color = LegendText.copy(alpha = 0.7f),
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
