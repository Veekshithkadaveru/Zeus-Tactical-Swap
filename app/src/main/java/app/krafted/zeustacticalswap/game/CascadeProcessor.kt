package app.krafted.zeustacticalswap.game

data class CascadeStep(
    val matches: List<Match>,
    val cascadeMultiplier: Float,
    val gridAfter: List<List<TileState>>,
    val newCells: Set<Pair<Int, Int>>
)

data class CascadeResult(
    val steps: List<CascadeStep>,
    val finalGrid: List<List<TileState>>
)

object CascadeProcessor {
    private const val MAX_ITERATIONS = 64

    fun process(grid: List<List<TileState>>): CascadeResult {
        val steps = mutableListOf<CascadeStep>()
        var current = grid
        var cascadeMultiplier = 1.0f
        var iterations = 0

        var matches = MatchDetector.findAllMatches(current)
        while (matches.isNotEmpty() && iterations < MAX_ITERATIONS) {
            val matchedCells = matches.flatMap { it.cells }.toSet()
            val cleared = GridEngine.clearMatches(current, matchedCells)
            val dropped = GridEngine.dropTiles(cleared)
            current = dropped.grid
            steps.add(
                CascadeStep(
                    matches = matches,
                    cascadeMultiplier = cascadeMultiplier,
                    gridAfter = current,
                    newCells = dropped.newCells
                )
            )
            cascadeMultiplier *= 1.5f
            iterations++
            matches = MatchDetector.findAllMatches(current)
        }

        return CascadeResult(steps, current)
    }
}
