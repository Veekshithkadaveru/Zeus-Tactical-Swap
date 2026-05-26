package app.krafted.zeustacticalswap.game

data class Match(
    val symbol: Symbol,
    val cells: List<Pair<Int, Int>>
)

object MatchDetector {
    fun findAllMatches(grid: List<List<TileState>>): List<Match> {
        val matches = mutableListOf<Match>()
        val size = grid.size

        for (r in 0 until size) {
            var c = 0
            while (c < size - 2) {
                val symbol = grid[r][c].symbol
                if (symbol == Symbol.SKULL) {
                    c++
                    continue
                }
                var len = 1
                while (c + len < size && grid[r][c + len].symbol == symbol) {
                    len++
                }
                if (len >= 3) {
                    matches.add(Match(symbol, (c until c + len).map { Pair(r, it) }))
                    c += len
                } else {
                    c++
                }
            }
        }

        for (c in 0 until size) {
            var r = 0
            while (r < size - 2) {
                val symbol = grid[r][c].symbol
                if (symbol == Symbol.SKULL) {
                    r++
                    continue
                }
                var len = 1
                while (r + len < size && grid[r + len][c].symbol == symbol) {
                    len++
                }
                if (len >= 3) {
                    matches.add(Match(symbol, (r until r + len).map { Pair(it, c) }))
                    r += len
                } else {
                    r++
                }
            }
        }

        return deduplicate(matches)
    }

    private fun deduplicate(matches: List<Match>): List<Match> {
        val claimed = mutableSetOf<Pair<Int, Int>>()
        val result = mutableListOf<Match>()
        for (match in matches) {
            val cells = match.cells.filter { it !in claimed }
            if (cells.isEmpty()) continue
            claimed.addAll(cells)
            result.add(if (cells.size == match.cells.size) match else match.copy(cells = cells))
        }
        return result
    }
}
