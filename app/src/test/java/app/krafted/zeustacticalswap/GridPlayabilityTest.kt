package app.krafted.zeustacticalswap

import app.krafted.zeustacticalswap.game.GridEngine
import app.krafted.zeustacticalswap.game.MatchDetector
import app.krafted.zeustacticalswap.game.Symbol
import app.krafted.zeustacticalswap.game.TileState
import org.junit.Assert.*
import org.junit.Test

class GridPlayabilityTest {

    @Test
    fun testHasPossibleMoves_withMatchableState() {
        // Create an 8x8 grid based on a diagonal pattern (no matches possible)
        // and manually override a small area to create exactly one valid swap.
        val gridWithMove = List(8) { r ->
            List(8) { c ->
                val symbol = when {
                    r == 0 && c == 0 -> Symbol.LIGHTNING
                    r == 0 && c == 1 -> Symbol.OWL
                    r == 0 && c == 2 -> Symbol.LIGHTNING
                    r == 0 && c == 3 -> Symbol.LIGHTNING
                    else -> Symbol.values()[(r + c) % 7]
                }
                TileState(symbol = symbol, id = r * 8 + c)
            }
        }

        assertTrue("Initial board should have no matches", MatchDetector.findAllMatches(gridWithMove).isEmpty())
        assertTrue("Board should have possible moves by swapping (0,0) and (0,1)", MatchDetector.hasPossibleMoves(gridWithMove))
    }

    @Test
    fun testHasPossibleMoves_withNoPossibleMoves() {
        // Create a grid using a diagonal cyclic pattern of the 7 symbols.
        // In this pattern, no two adjacent tiles have the same symbol, 
        // and swapping any two adjacent tiles creates at most a 2-match, never a 3-match.
        val gridWithNoMoves = List(8) { r ->
            List(8) { c ->
                val symbol = Symbol.values()[(r + c) % 7]
                TileState(symbol = symbol, id = r * 8 + c)
            }
        }

        assertTrue("Board should have no matches", MatchDetector.findAllMatches(gridWithNoMoves).isEmpty())
        assertFalse("Board should have no valid moves possible", MatchDetector.hasPossibleMoves(gridWithNoMoves))
    }

    @Test
    fun testMakePlayableGrid() {
        val grid = GridEngine.makePlayableGrid()
        assertEquals(8, grid.size)
        assertEquals(8, grid[0].size)
        assertTrue("A newly generated playable grid should have no initial matches", MatchDetector.findAllMatches(grid).isEmpty())
        assertTrue("A newly generated playable grid must have at least one valid move", MatchDetector.hasPossibleMoves(grid))
    }

    @Test
    fun testShufflePlayableGrid_preservesSkulls() {
        // Generate a grid and add some skulls
        val initialGrid = GridEngine.makePlayableGrid().mapIndexed { r, row ->
            row.mapIndexed { c, tile ->
                if ((r == 2 && c == 3) || (r == 4 && c == 5)) {
                    tile.copy(symbol = Symbol.SKULL)
                } else {
                    tile
                }
            }
        }

        val shuffled = GridEngine.shufflePlayableGrid(initialGrid)

        // Verify size
        assertEquals(8, shuffled.size)
        assertEquals(8, shuffled[0].size)

        // Verify skulls are in the exact same positions
        assertEquals(Symbol.SKULL, shuffled[2][3].symbol)
        assertEquals(Symbol.SKULL, shuffled[4][5].symbol)

        // Verify no immediate matches in the shuffled board
        assertTrue("Shuffled grid should have no matches", MatchDetector.findAllMatches(shuffled).isEmpty())
        
        // Verify we have at least one possible move
        assertTrue("Shuffled grid must have at least one possible move", MatchDetector.hasPossibleMoves(shuffled))
    }
}
