package app.krafted.zeustacticalswap.game

import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

object GridEngine {
    private val nextTileId = AtomicInteger(1)

    private fun getNextId(): Int = nextTileId.getAndIncrement()

    private val spawnableSymbols = Symbol.values().filter { it != Symbol.SKULL }

    fun randomSymbol(): Symbol = spawnableSymbols[Random.nextInt(spawnableSymbols.size)]

    fun makeTile(symbol: Symbol, isNew: Boolean = false): TileState {
        return TileState(symbol = symbol, id = getNextId(), isNew = isNew)
    }

    fun makeGrid(size: Int = 8): List<List<TileState>> {
        val grid = MutableList(size) { MutableList<TileState?>(size) { null } }
        for (r in 0 until size) {
            for (c in 0 until size) {
                var s: Symbol
                do {
                    s = randomSymbol()
                } while (
                    (c >= 2 && grid[r][c - 1]?.symbol == s && grid[r][c - 2]?.symbol == s) ||
                    (r >= 2 && grid[r - 1][c]?.symbol == s && grid[r - 2][c]?.symbol == s)
                )
                grid[r][c] = makeTile(s)
            }
        }
        @Suppress("UNCHECKED_CAST")
        return grid.map { it.toList() } as List<List<TileState>>
    }

    fun isAdjacent(a: Pair<Int, Int>, b: Pair<Int, Int>): Boolean {
        val dr = Math.abs(a.first - b.first)
        val dc = Math.abs(a.second - b.second)
        return (dr == 1 && dc == 0) || (dr == 0 && dc == 1)
    }

    fun swap(
        grid: List<List<TileState>>,
        a: Pair<Int, Int>,
        b: Pair<Int, Int>
    ): List<List<TileState>> {
        val size = grid.size
        if (a.first !in 0 until size || a.second !in 0 until size ||
            b.first !in 0 until size || b.second !in 0 until size
        ) {
            return grid
        }
        return grid.mapIndexed { r, row ->
            row.mapIndexed { c, tile ->
                when {
                    r == a.first && c == a.second -> grid[b.first][b.second]
                    r == b.first && c == b.second -> grid[a.first][a.second]
                    else -> tile
                }
            }
        }
    }

    fun clearMatches(
        grid: List<List<TileState>>,
        matchedCells: Set<Pair<Int, Int>>
    ): List<List<TileState>> {
        return grid.mapIndexed { r, row ->
            row.mapIndexed { c, tile ->
                if (Pair(r, c) in matchedCells) tile.copy(isMatched = true) else tile
            }
        }
    }

    data class DropResult(
        val grid: List<List<TileState>>,
        val newCells: Set<Pair<Int, Int>>
    )

    fun dropTiles(grid: List<List<TileState>>): DropResult {
        val size = grid.size
        val newGrid = MutableList(size) { r -> MutableList(size) { c -> grid[r][c] } }
        val newCells = mutableSetOf<Pair<Int, Int>>()
        for (c in 0 until size) {
            val remaining = (0 until size)
                .map { r -> grid[r][c] }
                .filter { !it.isMatched }
            val emptyCount = size - remaining.size
            for (r in 0 until size) {
                if (r < emptyCount) {
                    newGrid[r][c] = makeTile(randomSymbol(), isNew = true)
                    newCells.add(Pair(r, c))
                } else {
                    newGrid[r][c] = remaining[r - emptyCount].copy(
                        isNew = false,
                        isSelected = false,
                        isMatched = false
                    )
                }
            }
        }
        return DropResult(newGrid.map { it.toList() }, newCells)
    }
}
