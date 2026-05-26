package app.krafted.zeustacticalswap.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.krafted.zeustacticalswap.game.GridEngine
import app.krafted.zeustacticalswap.game.TileState

@Composable
fun GameGrid(
    grid: List<List<TileState>>,
    selectedTile: Pair<Int, Int>?,
    invalidSwapCells: Set<Pair<Int, Int>> = emptySet(),
    onTileTap: (row: Int, col: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        grid.forEachIndexed { row, tiles ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                tiles.forEachIndexed { col, tile ->
                    key(tile.id) {
                        TileCell(
                            tile = tile,
                            isSelected = selectedTile == (row to col),
                            isInvalidShake = (row to col) in invalidSwapCells,
                            onClick = { onTileTap(row, col) },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    }
                }
            }
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
        onTileTap = { _, _ -> }
    )
}
