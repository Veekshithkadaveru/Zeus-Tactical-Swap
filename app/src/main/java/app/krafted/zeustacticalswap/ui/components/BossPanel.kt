package app.krafted.zeustacticalswap.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
private val RageTint = Color(0x55EF4444)
private val CrackColour = Color(0xCC1F2937)

@Composable
fun BossPanel(boss: BossState, modifier: Modifier = Modifier) {
    val animatedFraction by animateFloatAsState(
        targetValue = boss.hpFraction.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600),
        label = "bossHp"
    )

    val rageTint by animateColorAsState(
        targetValue = if (boss.isEnraged) RageTint else Color.Transparent,
        animationSpec = tween(durationMillis = 300),
        label = "bossRageTint"
    )

    val isPetrified = boss.petrifyTurnsLeft > 0
    val petrifyAmount by animateFloatAsState(
        targetValue = if (isPetrified) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "bossPetrify"
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
            BossPortrait(
                backgroundRes = boss.backgroundRes,
                rageTint = rageTint,
                petrifyAmount = petrifyAmount
            )

            Text(
                text = boss.name,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            BossHpBar(
                fraction = animatedFraction,
                isEnraged = boss.isEnraged,
                isPoisoned = boss.poisonTurnsLeft > 0,
                currentHp = boss.currentHp,
                maxHp = boss.maxHp
            )

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
private fun BossPortrait(
    backgroundRes: Int,
    rageTint: Color,
    petrifyAmount: Float
) {
    val portraitFilter = if (petrifyAmount > 0f) {
        ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(1f - petrifyAmount) })
    } else {
        null
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Image(
            painter = painterResource(backgroundRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            colorFilter = portraitFilter,
            modifier = Modifier.fillMaxSize()
        )

        if (rageTint.alpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(rageTint)
            )
        }

        if (petrifyAmount > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        drawContent()
                        drawCracks(petrifyAmount)
                    }
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 2.dp,
                        color = CrackColour.copy(alpha = 0.8f * petrifyAmount),
                        shape = RoundedCornerShape(8.dp)
                    )
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCracks(amount: Float) {
    val w = size.width
    val h = size.height
    val crack = Path().apply {
        moveTo(w * 0.5f, 0f)
        lineTo(w * 0.45f, h * 0.35f)
        lineTo(w * 0.6f, h * 0.55f)
        lineTo(w * 0.5f, h)
        moveTo(w * 0.45f, h * 0.35f)
        lineTo(w * 0.2f, h * 0.5f)
        moveTo(w * 0.6f, h * 0.55f)
        lineTo(w * 0.85f, h * 0.7f)
    }
    drawPath(
        path = crack,
        color = CrackColour.copy(alpha = 0.85f * amount),
        style = Stroke(width = 2.5f)
    )
}

@Composable
private fun BossHpBar(
    fraction: Float,
    isEnraged: Boolean,
    isPoisoned: Boolean,
    currentHp: Int,
    maxHp: Int
) {
    val dripAlpha = if (isPoisoned) {
        val transition = rememberInfiniteTransition(label = "poisonDrip")
        val pulse by transition.animateFloat(
            initialValue = 0.15f,
            targetValue = 0.55f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 400),
                repeatMode = RepeatMode.Reverse
            ),
            label = "poisonDripPulse"
        )
        pulse
    } else {
        0f
    }

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
                .fillMaxHeight()
                .fillMaxWidth(fraction)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isEnraged) HpFillEnraged else HpFill)
            )
            if (dripAlpha > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PoisonColour.copy(alpha = dripAlpha))
                )
            }
        }
        Text(
            text = "$currentHp / $maxHp",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.Center)
        )
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
private fun BossPanelEnragedPreview() {
    val boss = BossState.forBoss(BossId.KRONOS)
        .takeDamage(260)
        .applyPoison(damagePerTurn = 8, turns = 2)
    BossPanel(boss = boss)
}

@Preview
@Composable
private fun BossPanelPetrifiedPreview() {
    val boss = BossState.forBoss(BossId.HADES)
        .takeDamage(400)
        .applyPetrify(turns = 3)
    BossPanel(boss = boss)
}
