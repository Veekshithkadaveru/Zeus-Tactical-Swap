package app.krafted.zeustacticalswap.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import app.krafted.zeustacticalswap.game.PlayerState
import app.krafted.zeustacticalswap.ui.theme.ChargePips
import app.krafted.zeustacticalswap.ui.theme.ChipTone
import app.krafted.zeustacticalswap.ui.theme.HpBar
import app.krafted.zeustacticalswap.ui.theme.HpKind
import app.krafted.zeustacticalswap.ui.theme.Zeus
import app.krafted.zeustacticalswap.ui.theme.ZeusChip
import app.krafted.zeustacticalswap.ui.theme.zeusPanel
import kotlin.math.roundToInt

@Composable
fun PlayerPanel(player: PlayerState, modifier: Modifier = Modifier) {
    val fraction = if (player.maxHp > 0) player.currentHp.toFloat() / player.maxHp else 0f
    val animatedHp by animateFloatAsState(
        targetValue = fraction.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600),
        label = "playerHp"
    )

    val shakeOffset = remember { Animatable(0f) }
    val previousHp = remember { intArrayOf(player.currentHp) }
    LaunchedEffect(player.currentHp) {
        if (player.currentHp < previousHp[0]) {
            shakeOffset.snapTo(12f)
            shakeOffset.animateTo(0f, spring(dampingRatio = 0.12f, stiffness = 700f))
        }
        previousHp[0] = player.currentHp
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .zeusPanel()
            .padding(start = 12.dp, end = 12.dp, top = 6.dp, bottom = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "HERO", style = Zeus.monoLabel(size = 9, color = Zeus.InkMute, tracking = 0.3))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (player.shieldHp > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "🛡", fontSize = 13.sp)
                        Text(
                            text = "${player.shieldHp}",
                            style = Zeus.monoLabel(size = 11, color = Zeus.Electric, tracking = 0.0)
                        )
                    }
                }
                Text(
                    text = buildAnnotatedString {
                        withStyle(Zeus.monoLabel(size = 11, color = Zeus.Ink, tracking = 0.0).toSpanStyle()) {
                            append("${player.currentHp}")
                        }
                        withStyle(Zeus.monoLabel(size = 11, color = Zeus.InkDim.copy(alpha = 0.6f), tracking = 0.0).toSpanStyle()) {
                            append(" / ${player.maxHp}")
                        }
                    }
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        HpBar(
            fraction = animatedHp,
            kind = HpKind.PLAYER,
            modifier = Modifier.offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "CHARGES", style = Zeus.monoLabel(size = 9, color = Zeus.InkMute, tracking = 0.18))
                ChargePips(charges = player.chargeCount)
            }
            AnimatedVisibility(visible = player.criticalActive) {
                val transition = rememberInfiniteTransition(label = "critPulse")
                val pulse by transition.animateFloat(
                    initialValue = 0.65f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
                    label = "critPulseAlpha"
                )
                Box(modifier = Modifier.alpha(pulse)) {
                    ZeusChip("✦ CRIT × ${player.criticalTurnsLeft}", ChipTone.CRIT)
                }
            }
        }
    }
}
