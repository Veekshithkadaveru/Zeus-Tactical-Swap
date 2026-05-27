package app.krafted.zeustacticalswap.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import app.krafted.zeustacticalswap.game.GridEngine
import app.krafted.zeustacticalswap.game.TileState

private val CriticalGold = Color(0xFFFBBF24)

@Composable
fun GameGrid(
    grid: List<List<TileState>>,
    selectedTile: Pair<Int, Int>?,
    invalidSwapCells: Set<Pair<Int, Int>> = emptySet(),
    onTileTap: (row: Int, col: Int) -> Unit,
    modifier: Modifier = Modifier,
    criticalActive: Boolean = false
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
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
            val shimmer by transition.animateFloat(
                initialValue = 0.6f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(700),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "criticalGridShimmer"
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .alpha(shimmer)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                CriticalGold.copy(alpha = 0.18f),
                                CriticalGold.copy(alpha = 0.08f),
                                CriticalGold.copy(alpha = 0.18f)
                            )
                        )
                    )
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
