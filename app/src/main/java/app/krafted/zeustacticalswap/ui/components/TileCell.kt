package app.krafted.zeustacticalswap.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.zeustacticalswap.game.Symbol
import app.krafted.zeustacticalswap.game.TileState
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** Per-symbol glow colour, mirroring `SYM[...].glow` in game.jsx. */
private fun glowColour(symbol: Symbol): Color = when (symbol) {
    Symbol.LIGHTNING -> Color(0xFF5DC9FF)
    Symbol.OWL -> Color(0xFF4FD49A)
    Symbol.TRIDENT -> Color(0xFF3AA1FF)
    Symbol.HELMET -> Color(0xFFD23C3C)
    Symbol.LAUREL -> Color(0xFFFFD770)
    Symbol.AMPHORA -> Color(0xFF9BE23A)
    Symbol.MEDUSA -> Color(0xFFB6B6B6)
    Symbol.SKULL -> Color(0xFF505050)
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
    val glow = glowColour(tile.symbol)
    val selected = isSelected && !isSkull

    // tilePop: scale 1 -> 1.35 (brighten) -> 0 (fade out) over ~380ms.
    val matchScale = remember(tile.id) { Animatable(1f) }
    val matchBright = remember(tile.id) { Animatable(0f) }
    val matchAlpha = remember(tile.id) { Animatable(1f) }
    LaunchedEffect(tile.id, tile.isMatched) {
        if (tile.isMatched) {
            val ease = CubicBezierEasing(0.5f, 1.5f, 0.4f, 1f)
            matchScale.snapTo(1f); matchBright.snapTo(0f); matchAlpha.snapTo(1f)
            // 0 -> 35%: grow to 1.35 and brighten
            matchScale.animateTo(1.35f, tween(133, easing = ease))
            matchBright.snapTo(1f)
            // 35% -> 100%: collapse to 0 while fading out
            launch { matchAlpha.animateTo(0f, tween(247)) }
            matchScale.animateTo(0f, tween(247, easing = ease))
        }
    }

    // drop: in from above with overshoot/settle (~420ms).
    val density = LocalDensity.current
    val dropStartPx = remember { with(density) { -120.dp.toPx() } }
    val dropOffsetY = remember(tile.id) { Animatable(if (tile.isNew) dropStartPx else 0f) }
    LaunchedEffect(tile.id) {
        if (tile.isNew) {
            dropOffsetY.animateTo(0f, spring(dampingRatio = 0.55f, stiffness = 350f))
        }
    }

    // shake: horizontal jitter (~320ms).
    val shakeOffsetX = remember { Animatable(0f) }
    LaunchedEffect(isInvalidShake) {
        if (isInvalidShake) {
            try {
                shakeOffsetX.animateTo(
                    targetValue = 0f,
                    animationSpec = keyframes {
                        durationMillis = 320
                        -7f at 64
                        6f at 128
                        -4f at 192
                        3f at 256
                        0f at 320
                    }
                )
            } finally {
                shakeOffsetX.snapTo(0f)
            }
        }
    }

    // tileSel: gentle opacity/scale pulse of the selected ring (~1.2s).
    val ringPulse = if (selected) {
        val t = rememberInfiniteTransition(label = "tileSel")
        t.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200),
                repeatMode = RepeatMode.Reverse
            ),
            label = "tileSelPulse"
        ).value
    } else 0f

    // skull glyph faint pulse.
    val skullPulse = if (isSkull) {
        val t = rememberInfiniteTransition(label = "skullPulse")
        t.animateFloat(
            initialValue = 0.65f,
            targetValue = 0.35f,
            animationSpec = infiniteRepeatable(
                animation = tween(900),
                repeatMode = RepeatMode.Reverse
            ),
            label = "skullPulseAlpha"
        ).value
    } else 0f

    val innerScale = if (selected) 1.12f else 1f
    val ringScale = 1f + 0.06f * ringPulse
    val ringAlpha = 0.9f + 0.1f * ringPulse

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .offset { IntOffset(shakeOffsetX.value.roundToInt(), dropOffsetY.value.roundToInt()) }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = !isSkull,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(tile.symbol.drawableRes),
            contentDescription = tile.symbol.label,
            contentScale = ContentScale.Fit,
            colorFilter = when {
                isSkull -> ColorFilter.colorMatrix(ColorMatrix().apply {
                    setToSaturation(0f)
                    timesAssign(ColorMatrix().apply { setToScale(0.5f, 0.5f, 0.5f, 1f) })
                })
                matchBright.value > 0f -> ColorFilter.colorMatrix(
                    ColorMatrix().apply {
                        val b = 1f + matchBright.value * 1.5f
                        setToScale(b, b, b, 1f)
                    }
                )
                else -> null
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .graphicsLayer {
                    val s = innerScale * matchScale.value
                    scaleX = s
                    scaleY = s
                    alpha = matchAlpha.value
                }
                .clip(RoundedCornerShape(8.dp))
        )

        if (selected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = ringScale
                        scaleY = ringScale
                        alpha = ringAlpha
                    }
                    .drawBehind {
                        val corner = CornerRadius(10.dp.toPx())
                        val stroke = 2.dp.toPx()
                        val inset = stroke / 2f
                        // glow halo
                        drawRoundRect(
                            color = glow.copy(alpha = 0.45f),
                            topLeft = Offset(inset, inset),
                            size = Size(size.width - inset * 2, size.height - inset * 2),
                            cornerRadius = corner,
                            style = Stroke(width = 6.dp.toPx())
                        )
                        // crisp ring
                        drawRoundRect(
                            color = Color(0xFFF7E9C4),
                            topLeft = Offset(inset, inset),
                            size = Size(size.width - inset * 2, size.height - inset * 2),
                            cornerRadius = corner,
                            style = Stroke(width = stroke)
                        )
                    }
            )
        }

        if (isSkull) {
            androidx.compose.material3.Text(
                text = "☠",
                style = TextStyle(
                    color = Color(0xFFC9C2A8).copy(alpha = skullPulse + 0.35f),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}