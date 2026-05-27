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
        val merged = mutableListOf<Match>()
        val remaining = matches.toMutableList()
        while (remaining.isNotEmpty()) {
            var current = remaining.removeAt(0)
            var mergedAny: Boolean
            do {
                mergedAny = false
                val iterator = remaining.iterator()
                while (iterator.hasNext()) {
                    val other = iterator.next()
                    if (current.symbol == other.symbol && current.cells.any { it in other.cells }) {
                        current = Match(current.symbol, (current.cells + other.cells).distinct())
                        iterator.remove()
                        mergedAny = true
                    }
                }
            } while (mergedAny)
            merged.add(current)
        }
        return merged
    }
}
