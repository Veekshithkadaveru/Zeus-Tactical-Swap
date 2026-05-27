package app.krafted.zeustacticalswap.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import app.krafted.zeustacticalswap.ui.theme.Zeus

@Composable
fun ActionLabel(text: String, colour: Color, modifier: Modifier = Modifier) {
    if (text.isBlank()) return

    val progress = remember { Animatable(0f) }
    LaunchedEffect(text) {
        progress.snapTo(0f)
        progress.animateTo(1f, animationSpec = tween(durationMillis = 1100))
    }

    val p = progress.value
    val alpha = when {
        p < 0.15f -> p / 0.15f
        p < 0.85f -> 1f
        else -> 1f - (p - 0.85f) / 0.15f
    }
    val scale = when {
        p < 0.15f -> 0.6f + (p / 0.15f) * 0.55f
        p < 0.85f -> 1.15f - ((p - 0.15f) / 0.7f) * 0.15f
        else -> 1f - (p - 0.85f) / 0.15f * 0.05f
    }
    val riseDp = (-p * 140f)

    Text(
        text = text,
        color = colour,
        fontFamily = Zeus.Display,
        fontWeight = FontWeight.Black,
        fontSize = 36.sp,
        style = TextStyle(
            shadow = Shadow(color = colour.copy(alpha = 0.9f), offset = Offset(0f, 0f), blurRadius = 28f)
        ),
        modifier = modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    placeable.placeRelative(0, riseDp.dp.roundToPx())
                }
            }
            .scale(scale)
            .alpha(alpha)
    )
}
