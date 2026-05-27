package app.krafted.zeustacticalswap.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.krafted.zeustacticalswap.game.GridEngine
import app.krafted.zeustacticalswap.game.TileState
import app.krafted.zeustacticalswap.ui.theme.Zeus

@Composable
fun GameGrid(
    grid: List<List<TileState>>,
    selectedTile: Pair<Int, Int>?,
    invalidSwapCells: Set<Pair<Int, Int>> = emptySet(),
    onTileTap: (row: Int, col: Int) -> Unit,
    modifier: Modifier = Modifier,
    criticalActive: Boolean = false
) {
    val gridLine = Zeus.Gold.copy(alpha = 0.05f)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .drawBehind {
                // radial dark board: lighter centre fading to near-black edges
                drawRoundRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF0F1228).copy(alpha = 0.65f), Color(0xFF02030C).copy(alpha = 0.95f)),
                        center = Offset(size.width / 2f, size.height / 2f),
                        radius = size.maxDimension * 0.7f
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx())
                )
                // faint inner shade
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.5f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                )
                // gold gridlines on an 8x8 cell grid
                val cell = size.width / 8f
                for (i in 1 until 8) {
                    val p = cell * i
                    drawLine(gridLine, Offset(p, 0f), Offset(p, size.height), 1f)
                    drawLine(gridLine, Offset(0f, p), Offset(size.width, p), 1f)
                }
            }
            .drawBehind {
                // 1dp gold border
                drawRoundRect(
                    color = Zeus.Gold.copy(alpha = 0.55f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                )
            }
    ) {
        val cellSize = maxWidth / 8

        grid.forEachIndexed { row, tiles ->
            tiles.forEachIndexed { col, tile ->
                key(tile.id) {
                    val targetX = cellSize * col
                    val targetY = cellSize * row

                    val animatedX by animateDpAsState(
                        targetValue = targetX,
                        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
                        label = "tileX"
                    )
                    val animatedY by animateDpAsState(
                        targetValue = targetY,
                        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
                        label = "tileY"
                    )

                    TileCell(
                        tile = tile,
                        isSelected = selectedTile == (row to col),
                        isInvalidShake = (row to col) in invalidSwapCells,
                        onClick = { onTileTap(row, col) },
                        modifier = Modifier
                            .size(cellSize)
                            .offset(x = animatedX, y = animatedY)
                    )
                }
            }
        }

        if (criticalActive) {
            val transition = rememberInfiniteTransition(label = "criticalGrid")
            val sweep by transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(animation = tween(1600, easing = LinearEasing)),
                label = "criticalShimmer"
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .drawBehind {
                        // diagonal gold highlight band sweeping continuously
                        val span = size.width + size.height
                        val pos = sweep * span * 2f - span
                        val band = size.minDimension * 0.45f
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFFFFE082).copy(alpha = 0.18f),
                                    Color.Transparent
                                ),
                                start = Offset(pos - band, pos - band),
                                end = Offset(pos + band, pos + band),
                                tileMode = TileMode.Clamp
                            ),
                            size = Size(size.width, size.height)
                        )
                    }
            )
        }
    }
}

@Preview
@Composable
private fun GameGridPreview() {
    GameGrid(
        grid = GridEngine.makeGrid(),
        selectedTile = 0 to 0,
        invalidSwapCells = emptySet(),
        onTileTap = { _, _ -> },
        criticalActive = true
    )
}
