package app.krafted.zeustacticalswap.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.zeustacticalswap.R
import kotlinx.coroutines.delay

private val Night = Color(0xFF04050F)
private val Gold = Color(0xFFE7B549)
private val GoldBright = Color(0xFFFFE49A)
private val InkMute = Color(0xFF7A745F)

@Composable
fun SplashScreen(
    onEnter: () -> Unit,
    modifier: Modifier = Modifier
) {
    var startAnim by remember { mutableStateOf(false) }
    var lightningTriggered by remember { mutableStateOf(false) }

    val infinite = rememberInfiniteTransition(label = "splash")

    val titleScale by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0.7f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "titleScale"
    )

    val emblemScale by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0.4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "emblemScale"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(durationMillis = 900),
        label = "contentAlpha"
    )

    val glowPulse by infinite.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    val loadingPulse by infinite.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loadingPulse"
    )

    val shakeX by animateDpAsState(
        targetValue = if (lightningTriggered) 5.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "shakeX"
    )

    val shakeY by animateDpAsState(
        targetValue = if (lightningTriggered) (-3).dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "shakeY"
    )

    LaunchedEffect(Unit) {
        startAnim = true
        delay(3500L)
        onEnter()
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2500L)
            lightningTriggered = true
            delay(120L)
            lightningTriggered = false
            delay(80L)
            lightningTriggered = true
            delay(100L)
            lightningTriggered = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Night),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.zeus_back_1),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (lightningTriggered) 0.85f else 0.5f),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            if (lightningTriggered) Color.White.copy(alpha = 0.25f) else Color.Transparent,
                            Night.copy(alpha = 0.75f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .offset(x = shakeX, y = shakeY)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .alpha(glowPulse)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Gold.copy(alpha = 0.55f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Image(
                        painter = painterResource(R.drawable.zeus_app_icon_round),
                        contentDescription = null,
                        modifier = Modifier
                            .size(160.dp)
                            .scale(emblemScale)
                            .clip(CircleShape)
                    )
                }

                Spacer(Modifier.height(28.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .scale(titleScale)
                        .alpha(contentAlpha)
                ) {
                    Text(
                        text = "OLYMPIANS ASCENDANT",
                        color = Gold.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "ZEUS",
                        color = Gold,
                        fontWeight = FontWeight.Black,
                        fontSize = 56.sp,
                        letterSpacing = 3.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "TACTICAL SWAP",
                        color = GoldBright,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        letterSpacing = 6.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .width(160.dp)
                            .height(1.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Gold.copy(alpha = 0.5f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(bottom = 40.dp)
                    .alpha(contentAlpha)
            ) {
                Text(
                    text = "ENTERING OLYMPUS",
                    color = GoldBright.copy(alpha = loadingPulse),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "MATCH 3 RPG · OFFLINE PROGRESSION",
                    color = InkMute,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    letterSpacing = 1.5.sp
                )
            }
        }
    }
}
