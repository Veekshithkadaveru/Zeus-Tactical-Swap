package app.krafted.zeustacticalswap.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import app.krafted.zeustacticalswap.ui.theme.Zeus

enum class FlashKind { ATTACK, HEAL, SHIELD, CRIT, POISON, PETRIFY }

@Composable
fun CombatEffectsOverlay(
    flash: FlashKind?,
    cascadeCount: Int,
    modifier: Modifier = Modifier
) {
    val flashAlpha = remember { Animatable(0f) }
    LaunchedEffect(flash) {
        if (flash != null) {
            flashAlpha.snapTo(0f)
            flashAlpha.animateTo(1f, androidx.compose.animation.core.tween(90))
            flashAlpha.animateTo(0f, androidx.compose.animation.core.tween(400))
        } else {
            flashAlpha.snapTo(0f)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (flash != null && flashAlpha.value > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(flashAlpha.value)
                    .background(flashBrush(flash))
            )
        }

        if (cascadeCount > 1) {
            ComboPill(count = cascadeCount, modifier = Modifier.align(Alignment.TopCenter).padding(top = 96.dp))
        }
    }
}

private fun flashBrush(kind: FlashKind): Brush = when (kind) {
    FlashKind.ATTACK -> Brush.radialGradient(
        colorStops = arrayOf(
            0.3f to Color.Transparent,
            0.8f to Color(0xFFFF3C3C).copy(alpha = 0.55f),
            1f to Color(0xFF780000).copy(alpha = 0.85f)
        ),
        center = Offset.Unspecified
    )
    FlashKind.HEAL -> Brush.radialGradient(
        colorStops = arrayOf(0f to Color(0xFF78FFA0).copy(alpha = 0.45f), 0.65f to Color.Transparent)
    )
    FlashKind.SHIELD -> Brush.radialGradient(
        colorStops = arrayOf(0f to Color(0xFF78C8FF).copy(alpha = 0.5f), 0.65f to Color.Transparent)
    )
    FlashKind.CRIT -> Brush.radialGradient(
        colorStops = arrayOf(0f to Color(0xFFFFE082).copy(alpha = 0.6f), 0.65f to Color.Transparent)
    )
    FlashKind.POISON -> Brush.radialGradient(
        colorStops = arrayOf(0f to Color(0xFF9BE23A).copy(alpha = 0.4f), 0.6f to Color.Transparent)
    )
    FlashKind.PETRIFY -> Brush.radialGradient(
        colorStops = arrayOf(0f to Color(0xFFC8C8C8).copy(alpha = 0.4f), 0.6f to Color.Transparent)
    )
}

@Composable
private fun ComboPill(count: Int, modifier: Modifier = Modifier) {
    val pop = remember { Animatable(0.4f) }
    LaunchedEffect(count) {
        pop.snapTo(0.4f)
        pop.animateTo(1.2f, androidx.compose.animation.core.tween(240))
        pop.animateTo(1f, androidx.compose.animation.core.tween(360))
    }
    Box(
        modifier = modifier
            .scale(pop.value)
            .clip(RoundedCornerShape(100.dp))
            .background(Zeus.goldFillBrush, RoundedCornerShape(100.dp))
            .border(1.dp, Zeus.GoldEdge, RoundedCornerShape(100.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = "COMBO ×$count",
            color = Zeus.ButtonInk,
            fontFamily = Zeus.Display,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            letterSpacing = 1.4.sp
        )
    }
}
