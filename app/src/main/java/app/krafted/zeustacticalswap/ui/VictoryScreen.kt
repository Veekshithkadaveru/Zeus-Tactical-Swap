package app.krafted.zeustacticalswap.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.zeustacticalswap.game.BossId
import app.krafted.zeustacticalswap.ui.theme.GoldButton
import app.krafted.zeustacticalswap.ui.theme.GoldRule
import app.krafted.zeustacticalswap.ui.theme.Zeus
import kotlin.random.Random

@Composable
fun VictoryScreen(boss: BossId, isFinalBoss: Boolean, onContinue: () -> Unit) {
    val firstName = boss.displayName.substringBefore(' ')

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Zeus.Night),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(boss.backgroundRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.35f),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x33E7B549), Color(0xF204050F)),
                        center = Offset.Unspecified
                    )
                )
        )
        RisingParticles(count = 24)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "⚡ VICTORY ⚡",
                style = Zeus.monoLabel(11, Zeus.Gold, tracking = 0.5)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                firstName.uppercase(),
                style = Zeus.goldHeading(40),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "HAS FALLEN",
                style = Zeus.goldHeading(16, tracking = 0.18)
            )
            Spacer(Modifier.height(18.dp))
            GoldRule(Modifier.width(200.dp))
            Spacer(Modifier.height(22.dp))

            Text("REWARD", style = Zeus.monoLabel(10, Zeus.InkDim, tracking = 0.22))
            Spacer(Modifier.height(8.dp))
            Text(
                "+ Divine Favor",
                color = Zeus.Ink,
                fontFamily = Zeus.Display,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(2.dp))
            Text("Full HP restored", style = Zeus.monoLabel(11, Zeus.InkMute, tracking = 0.0))

            Spacer(Modifier.height(36.dp))
            GoldButton(
                text = if (isFinalBoss) "Claim Olympus" else "Next Trial →",
                onClick = onContinue
            )
        }
    }
}

private data class Particle(val x: Float, val phase: Float, val speed: Float, val radius: Float)

@Composable
fun RisingParticles(count: Int, modifier: Modifier = Modifier) {
    val particles = remember(count) {
        val rng = Random(count * 31)
        List(count) {
            Particle(
                x = rng.nextFloat(),
                phase = rng.nextFloat(),
                speed = 0.7f + rng.nextFloat() * 0.6f,
                radius = 1.5f + rng.nextFloat() * 2.5f
            )
        }
    }
    val transition = rememberInfiniteTransition(label = "particles")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(2500, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "rise"
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { p ->
            val t = (progress * p.speed + p.phase) % 1f
            val y = size.height * (1f - t)
            // mirror the CSS rise: fade in early, fade out + shrink toward the top
            val alpha = when {
                t < 0.2f -> t / 0.2f
                else -> 1f - (t - 0.2f) / 0.8f
            }
            val scale = 1f - 0.6f * t
            drawCircle(
                color = Zeus.GoldHi.copy(alpha = alpha.coerceIn(0f, 1f) * 0.9f),
                radius = p.radius * scale,
                center = Offset(p.x * size.width, y)
            )
        }
    }
}

@Preview
@Composable
private fun VictoryScreenPreview() {
    VictoryScreen(boss = BossId.KRONOS, isFinalBoss = false, onContinue = {})
}

@Preview
@Composable
private fun VictoryFinalPreview() {
    VictoryScreen(boss = BossId.HADES, isFinalBoss = true, onContinue = {})
}
