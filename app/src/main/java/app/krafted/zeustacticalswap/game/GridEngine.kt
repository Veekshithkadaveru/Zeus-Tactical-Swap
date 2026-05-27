package app.krafted.zeustacticalswap.game

import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs
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
        val grid = MutableList(size) { MutableList(size) { makeTile(spawnableSymbols[0]) } }
        for (r in 0 until size) {
            for (c in 0 until size) {
                var s: Symbol
                do {
                    s = randomSymbol()
                } while (
                    (c >= 2 && grid[r][c - 1].symbol == s && grid[r][c - 2].symbol == s) ||
                    (r >= 2 && grid[r - 1][c].symbol == s && grid[r - 2][c].symbol == s)
                )
                grid[r][c] = makeTile(s)
            }
        }
        return grid.map { it.toList() }
    }

    /**
     * Checks if two grid coordinate pairs are orthogonally adjacent.
     */
    fun isAdjacent(a: Pair<Int, Int>, b: Pair<Int, Int>): Boolean {
        val dr = abs(a.first - b.first)
        val dc = abs(a.second - b.second)
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

    private fun wouldCreateMatch(grid: List<List<TileState>>, r: Int, c: Int, s: Symbol): Boolean {
        val size = grid.size
        fun getSym(row: Int, col: Int): Symbol? {
            if (row == r && col == c) return s
            if (row !in 0 until size || col !in 0 until size) return null
            return grid[row][col].symbol
        }

        if (getSym(r, c - 2) == s && getSym(r, c - 1) == s) return true
        if (getSym(r, c - 1) == s && getSym(r, c + 1) == s) return true
        if (getSym(r, c + 1) == s && getSym(r, c + 2) == s) return true

        if (getSym(r - 2, c) == s && getSym(r - 1, c) == s) return true
        if (getSym(r - 1, c) == s && getSym(r + 1, c) == s) return true
        if (getSym(r + 1, c) == s && getSym(r + 2, c) == s) return true

        return false
    }

    fun dropTiles(grid: List<List<TileState>>): DropResult {
        val size = grid.size
        val newGrid = MutableList(size) { r -> MutableList(size) { c -> grid[r][c] } }
        val newCells = mutableSetOf<Pair<Int, Int>>()

        // First, drop down the remaining tiles
        for (c in 0 until size) {
            val remaining = (0 until size)
                .map { r -> grid[r][c] }
                .filter { !it.isMatched }
            val emptyCount = size - remaining.size
            for (r in emptyCount until size) {
                newGrid[r][c] = remaining[r - emptyCount].copy(
                    isNew = false,
                    isSelected = false,
                    isMatched = false
                )
            }
        }

        // Now, spawn new tiles at the top, ensuring no immediate matches
        for (c in 0 until size) {
            val remaining = (0 until size)
                .map { r -> grid[r][c] }
                .filter { !it.isMatched }
            val emptyCount = size - remaining.size
            for (r in 0 until emptyCount) {
                var s: Symbol
                var attempts = 0
                do {
                    s = randomSymbol()
                    attempts++
                } while (wouldCreateMatch(newGrid.map { it.toList() }, r, c, s) && attempts < 20)

                newGrid[r][c] = makeTile(s, isNew = true)
                newCells.add(Pair(r, c))
            }
        }
        return DropResult(newGrid.map { it.toList() }, newCells)
    }
}
