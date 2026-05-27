package app.krafted.zeustacticalswap.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ActionLabel(text: String, colour: Color, modifier: Modifier = Modifier) {
    if (text.isBlank()) return

    val offsetY = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(text) {
        offsetY.snapTo(0f)
        alpha.snapTo(1f)
        offsetY.animateTo(-48f, animationSpec = tween(durationMillis = 700))
    }
    LaunchedEffect(text) {
        alpha.snapTo(1f)
        alpha.animateTo(0f, animationSpec = tween(durationMillis = 700))
    }

    Text(
        text = text,
        color = colour,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        letterSpacing = 1.sp,
        style = TextStyle(
            shadow = Shadow(
                color = Color.Black,
                offset = Offset(0f, 2f),
                blurRadius = 8f
            )
        ),
        modifier = modifier
            .offset { IntOffset(0, offsetY.value.dp.roundToPx()) }
            .alpha(alpha.value)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ActionLabelPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(Color(0xFF111827))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ActionLabel(text = "-42", colour = Color(0xFFDC2626))
            ActionLabel(text = "+18", colour = Color(0xFF4ADE80))
            ActionLabel(text = "CRITICAL!", colour = Color(0xFFFBBF24))
            Box(Modifier)
        }
    }
}
