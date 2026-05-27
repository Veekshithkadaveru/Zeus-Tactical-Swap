package app.krafted.zeustacticalswap.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

private val AttackRed = Color(0xFFDC2626)
private val HealGreen = Color(0xFF4ADE80)
private val ElectricBlue = Color(0xFF3B82F6)

@Composable
fun CombatEffectsOverlay(
    attackFlash: Boolean,
    healFlash: Boolean,
    cascadeLevel: Int,
    modifier: Modifier = Modifier
) {
    val attackAlpha by animateFloatAsState(
        targetValue = if (attackFlash) 0.55f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "attackFlash"
    )
    val healAlpha by animateFloatAsState(
        targetValue = if (healFlash) 0.4f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "healFlash"
    )

    val cascadeAlpha = remember { Animatable(0f) }
    LaunchedEffect(cascadeLevel) {
        if (cascadeLevel > 0) {
            val peak = (0.2f + cascadeLevel * 0.18f).coerceAtMost(0.8f)
            cascadeAlpha.snapTo(peak)
            cascadeAlpha.animateTo(0f, animationSpec = tween(durationMillis = 200))
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(attackAlpha)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.Transparent, AttackRed),
                        radius = 1400f
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(healAlpha)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(HealGreen, Color.Transparent, HealGreen)
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(cascadeAlpha.value)
                .background(
                    Brush.linearGradient(
                        colors = listOf(ElectricBlue, Color.Transparent, ElectricBlue),
                        start = Offset.Zero,
                        end = Offset.Infinite
                    )
                )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF111827, widthDp = 320, heightDp = 480)
@Composable
private fun CombatEffectsOverlayPreview() {
    MaterialTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xFF111827))
        ) {
            CombatEffectsOverlay(
                attackFlash = true,
                healFlash = false,
                cascadeLevel = 3
            )
        }
    }
}
