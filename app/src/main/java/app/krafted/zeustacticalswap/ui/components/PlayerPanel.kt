package app.krafted.zeustacticalswap.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.zeustacticalswap.game.PlayerState

private val PanelBg = Color(0xFF111827)
private val PanelBorder = Color(0xFF1F2937)
private val TrackColour = Color(0xFF374151)
private val HealthColour = Color(0xFF4ADE80)
private val HealthLowColour = Color(0xFFDC2626)
private val ShieldColour = Color(0xFF3B82F6)
private val ChargeColour = Color(0xFFF97316)
private val CriticalGold = Color(0xFFFBBF24)
private val TextPrimary = Color(0xFFE5E7EB)
private val TextMuted = Color(0xFF9CA3AF)

@Composable
fun PlayerPanel(player: PlayerState, modifier: Modifier = Modifier) {
    val hpFraction = if (player.maxHp > 0) {
        player.currentHp.toFloat() / player.maxHp.toFloat()
    } else {
        0f
    }
    val animatedHp by animateFloatAsState(
        targetValue = hpFraction.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500),
        label = "playerHp"
    )
    val healthTint = if (hpFraction <= 0.25f) HealthLowColour else HealthColour

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = PanelBg,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, PanelBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "YOU",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "${player.currentHp} / ${player.maxHp}",
                    color = healthTint,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }

            Spacer(Modifier.height(6.dp))

            val shieldRingScale by animateFloatAsState(
                targetValue = if (player.shieldHp > 0) 1f else 0f,
                animationSpec = spring(stiffness = 500f),
                label = "shieldRing"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .scale(scaleX = 1f, scaleY = 1f + shieldRingScale * 0.18f)
                    .clip(RoundedCornerShape(7.dp))
                    .background(TrackColour)
                    .border(
                        width = (2.5f * shieldRingScale).dp,
                        color = ShieldColour.copy(alpha = shieldRingScale * 0.9f),
                        shape = RoundedCornerShape(7.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedHp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(healthTint)
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ChargeMeter(chargeCount = player.chargeCount)
                AnimatedVisibility(visible = player.shieldHp > 0) {
                    ShieldBadge(shieldHp = player.shieldHp)
                }
            }

            AnimatedVisibility(visible = player.criticalActive) {
                Column {
                    Spacer(Modifier.height(8.dp))
                    CriticalIndicator(turnsLeft = player.criticalTurnsLeft)
                }
            }
        }
    }
}

@Composable
private fun ChargeMeter(chargeCount: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "CHARGE",
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )
        Spacer(Modifier.width(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            repeat(3) { index ->
                val filled = index < chargeCount
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(if (filled) ChargeColour else TrackColour)
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = "$chargeCount/3",
            color = TextMuted,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun ShieldBadge(shieldHp: Int) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(ShieldColour.copy(alpha = 0.18f))
            .border(1.dp, ShieldColour, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Shield: $shieldHp",
            color = ShieldColour,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun CriticalIndicator(turnsLeft: Int) {
    val transition = rememberInfiniteTransition(label = "critical")
    val shimmer by transition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "criticalShimmer"
    )
    val label = if (turnsLeft > 0) "CRITICAL ACTIVE ($turnsLeft)" else "CRITICAL READY!"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        CriticalGold.copy(alpha = 0.12f),
                        CriticalGold.copy(alpha = 0.28f),
                        CriticalGold.copy(alpha = 0.12f)
                    )
                )
            )
            .border(1.dp, CriticalGold.copy(alpha = shimmer), RoundedCornerShape(8.dp))
            .alpha(shimmer)
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "✨ $label",
            color = CriticalGold,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            letterSpacing = 1.sp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PlayerPanelPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(Color.Black)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PlayerPanel(
                player = PlayerState(
                    maxHp = 120,
                    currentHp = 85,
                    shieldHp = 24,
                    chargeCount = 2
                )
            )
            PlayerPanel(
                player = PlayerState(
                    maxHp = 120,
                    currentHp = 30,
                    shieldHp = 0,
                    chargeCount = 0,
                    criticalActive = true,
                    criticalTurnsLeft = 2
                )
            )
        }
    }
}
