package app.krafted.zeustacticalswap.game

object CombatResolver {

    fun resolveMatch(symbol: Symbol, matchSize: Int, criticalActive: Boolean): CombatResult {
        return when (symbol) {
            Symbol.LIGHTNING -> CombatResult.Attack(
                damage = matchSize * 15 * if (criticalActive) 3 else 1
            )

            Symbol.OWL -> CombatResult.Heal(
                amount = matchSize * 10
            )

            Symbol.TRIDENT -> CombatResult.Shield(
                absorption = matchSize * 12
            )

            Symbol.HELMET -> CombatResult.Charge(
                chargesAdded = (matchSize - 2).coerceAtLeast(1)
            )

            Symbol.LAUREL -> CombatResult.CriticalStrike(
                turnsActive = matchSize - 1
            )

            Symbol.AMPHORA -> CombatResult.Poison(
                damagePerTurn = matchSize * 8,
                turnsRemaining = 3
            )

            Symbol.MEDUSA -> CombatResult.Petrify(
                turnsRemaining = if (matchSize >= 5) 3 else 2
            )

            Symbol.SKULL -> CombatResult.Attack(damage = 0)
        }
    }

    fun resolveAllMatches(
        matches: List<Match>,
        criticalActive: Boolean,
        cascadeMultiplier: Float
    ): List<CombatResult> {
        return matches.map { match ->
            resolveMatch(match.symbol, match.cells.size, criticalActive)
                .scale(cascadeMultiplier)
        }
    }

    fun applyCombatResults(
        results: List<CombatResult>,
        player: PlayerState,
        bossState: BossState
    ): Pair<PlayerState, BossState> {
        var p = player
        var b = bossState
        for (result in results) {
            when (result) {
                is CombatResult.Attack -> b = b.takeDamage(result.damage)
                is CombatResult.Heal -> p = p.heal(result.amount)
                is CombatResult.Shield -> p = p.addShield(result.absorption)
                is CombatResult.Charge -> p = p.addCharge(result.chargesAdded)
                is CombatResult.CriticalStrike -> p = p.activateCritical(result.turnsActive)
                is CombatResult.Poison -> {
                    b = b.applyPoison(result.damagePerTurn, result.turnsRemaining)
                }

                is CombatResult.Petrify -> {
                    b = b.applyPetrify(result.turnsRemaining)
                }
            }
        }
        return Pair(p, b)
    }
}
