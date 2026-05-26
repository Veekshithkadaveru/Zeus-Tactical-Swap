package app.krafted.zeustacticalswap.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.zeustacticalswap.game.BossId
import app.krafted.zeustacticalswap.game.BossState

private val PanelBackground = Color(0xFF111827)
private val PanelBorder = Color(0xFF374151)
private val HpTrack = Color(0xFF3F1D1D)
private val HpFill = Color(0xFFDC2626)
private val HpFillEnraged = Color(0xFFF87171)
private val TextPrimary = Color(0xFFE5E7EB)
private val RageColour = Color(0xFFB91C1C)
private val PoisonColour = Color(0xFF4ADE80)
private val PetrifyColour = Color(0xFF9CA3AF)

@Composable
fun BossPanel(boss: BossState, modifier: Modifier = Modifier) {
    val animatedFraction by animateFloatAsState(
        targetValue = boss.hpFraction.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600),
        label = "bossHp"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = PanelBackground,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .border(1.dp, PanelBorder, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Text(
                text = boss.name,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(HpTrack)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedFraction)
                        .fillMaxSize()
                        .background(if (boss.isEnraged) HpFillEnraged else HpFill)
                )
                Text(
                    text = "${boss.currentHp} / ${boss.maxHp}",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            val hasStatus = boss.isEnraged ||
                    boss.poisonTurnsLeft > 0 ||
                    boss.petrifyTurnsLeft > 0
            if (hasStatus) {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (boss.isEnraged) {
                        StatusBadge(label = "RAGE", colour = RageColour)
                    }
                    if (boss.poisonTurnsLeft > 0) {
                        StatusBadge(
                            label = "Poisoned (${boss.poisonTurnsLeft})",
                            colour = PoisonColour
                        )
                    }
                    if (boss.petrifyTurnsLeft > 0) {
                        StatusBadge(
                            label = "Petrified (${boss.petrifyTurnsLeft})",
                            colour = PetrifyColour
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(label: String, colour: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(colour.copy(alpha = 0.22f))
            .border(1.dp, colour, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            color = colour,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}

@Preview
@Composable
private fun BossPanelPreview() {
    val boss = BossState.forBoss(BossId.KRONOS)
        .takeDamage(260)
        .applyPoison(damagePerTurn = 8, turns = 2)
    BossPanel(boss = boss)
}
