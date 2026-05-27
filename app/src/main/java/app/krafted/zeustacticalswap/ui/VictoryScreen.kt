package app.krafted.zeustacticalswap.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.zeustacticalswap.game.BossId
import kotlin.random.Random

private val Gold = Color(0xFFE7B549)
private val GoldBright = Color(0xFFFFE49A)
private val Ink = Color(0xFFEDE7D6)
private val InkDim = Color(0xFFB8AE97)
private val InkMute = Color(0xFF7A745F)

@Composable
fun VictoryScreen(boss: BossId, isFinalBoss: Boolean, onContinue: () -> Unit) {
    val firstName = boss.displayName.substringBefore(' ')

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF04050F)),
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
        SparkleField()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Text(
                "⚡ VICTORY ⚡",
                color = Gold,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 5.5.sp
            )
            Spacer(Modifier.height(10.dp))
            Text(
                firstName.uppercase(),
                color = Gold,
                fontWeight = FontWeight.Black,
                fontSize = 40.sp,
                letterSpacing = 1.5.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "HAS FALLEN",
                color = GoldBright,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                letterSpacing = 3.sp
            )
            Spacer(Modifier.height(18.dp))
            GoldRule(Modifier.width(200.dp))
            Spacer(Modifier.height(22.dp))

            Text(
                "REWARD",
                color = InkDim,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                letterSpacing = 2.2.sp
            )
            Spacer(Modifier.height(8.dp))
            Text("+ Divine Favor", color = Ink, fontWeight = FontWeight.Black, fontSize = 18.sp)
            Spacer(Modifier.height(2.dp))
            Text(
                "Full HP restored",
                color = InkMute,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
            )

            Spacer(Modifier.height(36.dp))
            Button(
                onClick = onContinue,
                modifier = Modifier.height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = Color(0xFF1A1303)
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 32.dp)
            ) {
                Text(
                    if (isFinalBoss) "Claim Olympus" else "Next Trial →",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}

@Composable
private fun SparkleField() {
    val transition = rememberInfiniteTransition(label = "sparkles")
    val particles = remember {
        List(24) {
            Triple(Random.nextFloat(), Random.nextFloat(), 3f + Random.nextFloat() * 4f)
        }
    }
    Box(Modifier.fillMaxSize()) {
        particles.forEachIndexed { index, (xFrac, phase, sizeDp) ->
            val twinkle by transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    tween(1600 + (phase * 1400).toInt(), easing = LinearEasing),
                    RepeatMode.Reverse
                ),
                label = "twinkle$index"
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(
                        start = (xFrac * 320).dp,
                        top = (phase * 600).dp
                    )
            ) {
                Box(
                    Modifier
                        .size(sizeDp.dp)
                        .alpha(twinkle)
                        .clip(CircleShape)
                        .background(GoldBright)
                )
            }
        }
    }
}

@Composable
private fun GoldRule(modifier: Modifier = Modifier) {
    Box(
        modifier
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(Color.Transparent, GoldBright.copy(alpha = 0.6f), Color.Transparent)
                )
            )
    )
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
