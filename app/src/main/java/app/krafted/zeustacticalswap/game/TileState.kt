package app.krafted.zeustacticalswap.game

data class TileState(
    val symbol: Symbol,
    val id: Int,
    val isSelected: Boolean = false,
    val isMatched: Boolean = false,
    val isNew: Boolean = false
)
