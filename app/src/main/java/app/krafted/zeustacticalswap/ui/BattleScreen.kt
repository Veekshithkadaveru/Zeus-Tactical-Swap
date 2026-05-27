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

private val symbolGlow: Map<Symbol, Color> = mapOf(
    Symbol.LIGHTNING to Color(0xFF5DC9FF),
    Symbol.OWL to Color(0xFF4FD49A),
    Symbol.TRIDENT to Color(0xFF3AA1FF),
    Symbol.HELMET to Color(0xFFD23C3C),
    Symbol.LAUREL to Color(0xFFFFD770),
    Symbol.AMPHORA to Color(0xFF9BE23A),
    Symbol.MEDUSA to Color(0xFFB6B6B6)
)

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

    val lastActionColor = actionColorFor(state.lastActionText, state.phase)
    val flash = flashFor(state.lastActionText, state.phase, state.boss.petrifyTurnsLeft > 0)

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
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .border(1.dp, Zeus.Gold.copy(alpha = 0.35f), RoundedCornerShape(100.dp))
                        .clickable(onClick = onQuit)
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "← FLEE",
                        style = Zeus.monoLabel(size = 11, color = Zeus.InkDim, tracking = 0.16)
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
                            .size(28.dp)
                            .clip(CircleShape)
                            .border(1.dp, Zeus.Gold.copy(alpha = 0.45f), CircleShape)
                            .clickable { showRules = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "?", color = Zeus.InkDim, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            BossPanel(
                boss = state.boss,
                trialNumber = state.currentBossIndex + 1,
                modifier = Modifier.padding(top = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp),
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
                    .aspectRatio(1f),
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
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        CombatEffectsOverlay(
            flash = flash,
            cascadeCount = if (state.cascadeCount > 0) state.cascadeCount + 1 else 0
        )

        if (state.phase == TurnPhase.RESOLVING_MATCHES && state.lastActionText.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                ActionLabel(text = state.lastActionText, colour = lastActionColor)
            }
        }
    }
}

private fun actionColorFor(text: String, phase: TurnPhase): Color {
    val t = text.uppercase()
    return when {
        t.contains("HEAL") || t.contains("+") && t.contains("HP") -> Zeus.Emerald
        t.contains("SHIELD") || t.contains("ABSORB") -> Zeus.Electric
        t.contains("POISON") -> Zeus.Poison
        t.contains("PETRIF") -> Zeus.Stone
        t.contains("CRIT") || t.contains("CHARGE") -> Zeus.GoldHi
        t.contains("DMG") || t.contains("STRIKE") || phase == TurnPhase.BOSS_ATTACK -> Zeus.Crimson
        phase == TurnPhase.BOSS_SPECIAL -> Zeus.GoldHi
        else -> Zeus.InkDim
    }
}

private fun flashFor(text: String, phase: TurnPhase, petrified: Boolean): FlashKind? {
    val t = text.uppercase()
    return when {
        phase == TurnPhase.BOSS_ATTACK && !petrified -> FlashKind.ATTACK
        phase != TurnPhase.RESOLVING_MATCHES && phase != TurnPhase.APPLYING_COMBAT -> null
        t.contains("HEAL") -> FlashKind.HEAL
        t.contains("SHIELD") || t.contains("ABSORB") -> FlashKind.SHIELD
        t.contains("CRIT") -> FlashKind.CRIT
        t.contains("POISON") -> FlashKind.POISON
        t.contains("PETRIF") -> FlashKind.PETRIFY
        t.contains("DMG") -> FlashKind.ATTACK
        else -> null
    }
}

@Composable
private fun SymbolLegend() {
    val symbols = listOf(
        Symbol.LIGHTNING, Symbol.OWL, Symbol.TRIDENT, Symbol.HELMET,
        Symbol.LAUREL, Symbol.AMPHORA, Symbol.MEDUSA
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        symbols.forEach { sym ->
            val glow = symbolGlow[sym] ?: Zeus.Gold
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .border(1.dp, glow.copy(alpha = 0.55f), RoundedCornerShape(6.dp))
                    .padding(top = 4.dp, bottom = 5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Image(
                    painter = painterResource(sym.drawableRes),
                    contentDescription = sym.label,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = sym.label,
                    color = glow,
                    fontFamily = Zeus.Mono,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.7.sp
                )
            }
        }
    }
}
