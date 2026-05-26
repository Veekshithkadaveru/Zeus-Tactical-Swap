package app.krafted.zeustacticalswap.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import app.krafted.zeustacticalswap.game.Symbol
import app.krafted.zeustacticalswap.game.TileState
import kotlin.math.roundToInt

private fun actionColour(symbol: Symbol): Color = when (symbol) {
    Symbol.LIGHTNING -> Color(0xFF3B82F6)
    Symbol.OWL -> Color(0xFF4ADE80)
    Symbol.TRIDENT -> Color(0xFF0EA5E9)
    Symbol.HELMET -> Color(0xFFDC2626)
    Symbol.LAUREL -> Color(0xFFFBBF24)
    Symbol.AMPHORA -> Color(0xFF65A30D)
    Symbol.MEDUSA -> Color(0xFF9CA3AF)
    Symbol.SKULL -> Color(0xFF374151)
}

@Composable
fun TileCell(
    tile: TileState,
    isSelected: Boolean,
    isInvalidShake: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSkull = tile.symbol == Symbol.SKULL
    val borderColour = actionColour(tile.symbol)

    val matchScale = remember(tile.id) { Animatable(1f) }
    LaunchedEffect(tile.id, tile.isMatched) {
        if (tile.isMatched) {
            matchScale.animateTo(1.3f, spring(stiffness = Spring.StiffnessHigh))
            matchScale.animateTo(0f, tween(150))
        }
    }

    val density = LocalDensity.current
    val dropStartOffsetPx = remember { with(density) { -80.dp.toPx() } }
    val dropOffsetY = remember(tile.id) { Animatable(if (tile.isNew) dropStartOffsetPx else 0f) }
    LaunchedEffect(tile.id) {
        if (tile.isNew) {
            dropOffsetY.animateTo(
                0f,
                spring(dampingRatio = 0.7f, stiffness = 400f)
            )
        }
    }

    val shakeOffsetX = remember { Animatable(0f) }
    LaunchedEffect(isInvalidShake) {
        if (isInvalidShake) {
            try {
                shakeOffsetX.animateTo(
                    targetValue = 0f,
                    animationSpec = keyframes {
                        durationMillis = 300
                        12f at 50
                        -12f at 100
                        8f at 150
                        -8f at 200
                        4f at 250
                        0f at 300
                    }
                )
            } finally {
                shakeOffsetX.snapTo(0f)
            }
        }
    }

    val selected = isSelected && !isSkull
    val pulseScale = if (selected) {
        val pulseTransition = rememberInfiniteTransition(label = "pulse")
        val scale by pulseTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(450),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseScale"
        )
        scale
    } else {
        1.0f
    }

    Image(
        painter = painterResource(tile.symbol.drawableRes),
        contentDescription = tile.symbol.label,
        contentScale = ContentScale.Fit,
        colorFilter = if (isSkull) ColorFilter.tint(Color(0xFF4B5563)) else null,
        modifier = modifier
            .aspectRatio(1f)
            .offset {
                IntOffset(shakeOffsetX.value.roundToInt(), dropOffsetY.value.roundToInt())
            }
            .graphicsLayer {
                val s = matchScale.value * pulseScale
                scaleX = s
                scaleY = s
            }
            .clip(RoundedCornerShape(8.dp))
            .then(
                if (selected) Modifier
                    .background(borderColour.copy(alpha = 0.18f), RoundedCornerShape(8.dp))
                    .border(3.dp, borderColour, RoundedCornerShape(8.dp))
                else Modifier
            )
            .alpha(if (isSkull) 0.65f else 1f)
            .padding(4.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    )
}
