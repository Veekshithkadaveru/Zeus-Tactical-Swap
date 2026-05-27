package app.krafted.zeustacticalswap.game

sealed class CombatResult {
    abstract fun scale(multiplier: Float): CombatResult

    data class Attack(val damage: Int) : CombatResult() {
        override fun scale(multiplier: Float) = copy(damage = (damage * multiplier).toInt().coerceAtLeast(1))
    }

    data class Heal(val amount: Int) : CombatResult() {
        override fun scale(multiplier: Float) = copy(amount = (amount * multiplier).toInt().coerceAtLeast(1))
    }

    data class Shield(val absorption: Int) : CombatResult() {
        override fun scale(multiplier: Float) = copy(absorption = (absorption * multiplier).toInt().coerceAtLeast(1))
    }

    data class Charge(val chargesAdded: Int) : CombatResult() {
        override fun scale(multiplier: Float) =
            copy(chargesAdded = (chargesAdded * multiplier).toInt().coerceAtLeast(1))
    }

    data class CriticalStrike(val turnsActive: Int) : CombatResult() {
        override fun scale(multiplier: Float) = this
    }

    data class Poison(val damagePerTurn: Int, val turnsRemaining: Int) : CombatResult() {
        override fun scale(multiplier: Float) =
            copy(damagePerTurn = (damagePerTurn * multiplier).toInt().coerceAtLeast(1))
    }

    data class Petrify(val turnsRemaining: Int) : CombatResult() {
        override fun scale(multiplier: Float) = this
    }
}
