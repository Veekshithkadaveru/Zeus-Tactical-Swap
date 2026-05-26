package app.krafted.zeustacticalswap.game

import kotlin.random.Random

object GridEngine {
    private var nextTileId = 1

    private fun getNextId(): Int = nextTileId++

    private val spawnableSymbols = Symbol.values().filter { it != Symbol.SKULL }

    fun randomSymbol(): Symbol = spawnableSymbols[Random.nextInt(spawnableSymbols.size)]

    fun makeTile(symbol: Symbol, isNew: Boolean = false): TileState {
        return TileState(symbol = symbol, id = getNextId(), isNew = isNew)
    }

    fun makeGrid(size: Int = 8): List<List<TileState>> {
        val grid = MutableList(size) { MutableList(size) { makeTile(Symbol.LIGHTNING) } }
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
        return grid
    }

    fun isAdjacent(a: Pair<Int, Int>, b: Pair<Int, Int>): Boolean {
        val dr = Math.abs(a.first - b.first)
        val dc = Math.abs(a.second - b.second)
        return (dr == 1 && dc == 0) || (dr == 0 && dc == 1)
    }

    fun swap(grid: List<List<TileState>>, a: Pair<Int, Int>, b: Pair<Int, Int>): List<List<TileState>> {
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
}
