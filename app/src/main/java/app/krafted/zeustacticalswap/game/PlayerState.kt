package app.krafted.zeustacticalswap.game

data class PlayerState(
    val maxHp: Int = 120,
    val currentHp: Int = 120,
    val shieldHp: Int = 0,
    val chargeCount: Int = 0,
    val criticalActive: Boolean = false,
    val criticalTurnsLeft: Int = 0
) {
    val isAlive: Boolean get() = currentHp > 0

    fun takeDamage(damage: Int): PlayerState {
        val absorbed = minOf(shieldHp, damage)
        return copy(
            shieldHp = shieldHp - absorbed,
            currentHp = maxOf(0, currentHp - (damage - absorbed))
        )
    }

    fun heal(amount: Int): PlayerState {
        return copy(currentHp = minOf(maxHp, currentHp + amount))
    }

    fun addShield(amount: Int): PlayerState {
        return copy(shieldHp = shieldHp + amount)
    }

    fun addCharge(count: Int): PlayerState {
        val newCharges = chargeCount + count
        return if (newCharges >= 3) {
            copy(
                chargeCount = 0,
                criticalActive = true,
                criticalTurnsLeft = maxOf(criticalTurnsLeft, 2)
            )
        } else {
            copy(chargeCount = newCharges)
        }
    }

    fun activateCritical(turns: Int): PlayerState {
        return copy(criticalActive = true, criticalTurnsLeft = criticalTurnsLeft + turns)
    }

    fun onTurnEnd(): PlayerState {
        var state = this
        if (state.criticalActive) {
            val newTurns = state.criticalTurnsLeft - 1
            state = if (newTurns <= 0) {
                state.copy(criticalActive = false, criticalTurnsLeft = 0)
            } else {
                state.copy(criticalTurnsLeft = newTurns)
            }
        }
        return state
    }
}
