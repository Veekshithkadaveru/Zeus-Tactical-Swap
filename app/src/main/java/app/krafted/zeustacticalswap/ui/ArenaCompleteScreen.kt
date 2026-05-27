package app.krafted.zeustacticalswap.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.zeustacticalswap.R
import app.krafted.zeustacticalswap.ui.theme.GoldButton
import app.krafted.zeustacticalswap.ui.theme.GoldRule
import app.krafted.zeustacticalswap.ui.theme.Zeus

@Composable
fun ArenaCompleteScreen(
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var revealed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { revealed = true }

    val contentScale by animateFloatAsState(
        targetValue = if (revealed) 1f else 0.7f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = 300f
        ),
        label = "arenaScale"
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(700),
        label = "arenaAlpha"
    )

    val infinite = rememberInfiniteTransition(label = "arenaFinale")
    val glowPulse by infinite.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "arenaGlow"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Zeus.Night),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.zeus_back_5),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.5f),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Zeus.Gold.copy(alpha = 0.20f + glowPulse * 0.18f),
                            Zeus.Night
                        )
                    )
                )
        )

        RisingParticles(count = 36)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp)
                .scale(contentScale)
                .alpha(contentAlpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "⚡ ASCENSION ⚡",
                style = Zeus.monoLabel(12, Zeus.Gold, tracking = 0.55)
            )
            Spacer(Modifier.height(14.dp))
            Text(
                "OLYMPUS\nCONQUERED",
                style = Zeus.goldHeading(38, tracking = 0.06),
                lineHeight = 40.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(22.dp))
            GoldRule(Modifier.width(200.dp))
            Spacer(Modifier.height(22.dp))
            Text(
                "KRONOS · TYPHON · HADES",
                style = Zeus.monoLabel(11, Zeus.InkDim, tracking = 0.2),
                lineHeight = 19.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "ALL THREE TRIALS · ENDURED",
                style = Zeus.monoLabel(11, Zeus.InkMute, tracking = 0.2),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(40.dp))
            GoldButton(
                text = "Return to Olympus",
                onClick = onHome,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun ArenaCompleteScreenPreview() {
    ArenaCompleteScreen(onHome = {})
}
