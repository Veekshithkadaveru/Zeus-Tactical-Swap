package app.krafted.zeustacticalswap.game

import app.krafted.zeustacticalswap.R

enum class BossId(
    val displayName: String,
    val maxHp: Int,
    val minAttack: Int,
    val maxAttack: Int,
    val backgroundRes: Int,
    val specialInterval: Int
) {
    KRONOS("Kronos the Titan", 500, 25, 35, R.drawable.zeus_back_1, 0),
    TYPHON("Typhon the Storm", 800, 35, 50, R.drawable.zeus_back_2, 5),
    HADES("Hades the Eternal", 1200, 50, 70, R.drawable.zeus_back_3, 4)
}

data class BossState(
    val id: BossId = BossId.KRONOS,
    val name: String = BossId.KRONOS.displayName,
    val maxHp: Int = BossId.KRONOS.maxHp,
    val currentHp: Int = BossId.KRONOS.maxHp,
    val minAttack: Int = BossId.KRONOS.minAttack,
    val maxAttack: Int = BossId.KRONOS.maxAttack,
    val backgroundRes: Int = BossId.KRONOS.backgroundRes,
    val turnCount: Int = 0,
    val isEnraged: Boolean = false,
    val poisonDamage: Int = 0,
    val poisonTurnsLeft: Int = 0,
    val petrifyTurnsLeft: Int = 0
) {
    val isDefeated: Boolean get() = currentHp <= 0
    val hpFraction: Float get() = currentHp.toFloat() / maxHp.toFloat()

    fun takeDamage(damage: Int): BossState {
        val newHp = maxOf(0, currentHp - damage)
        return copy(
            currentHp = newHp,
            isEnraged = id == BossId.KRONOS && newHp < maxHp * 0.5f
        )
    }

    fun applyPoison(damagePerTurn: Int, turns: Int): BossState {
        return copy(poisonDamage = damagePerTurn, poisonTurnsLeft = turns)
    }

    fun applyPetrify(turns: Int): BossState {
        return copy(petrifyTurnsLeft = turns)
    }

    fun tickStatusEffects(): Pair<BossState, Int> {
        var b = this
        var damageDealt = 0
        if (b.poisonTurnsLeft > 0) {
            damageDealt = b.poisonDamage
            b = b.takeDamage(damageDealt)
            val newTurns = b.poisonTurnsLeft - 1
            b = b.copy(
                poisonTurnsLeft = newTurns,
                poisonDamage = if (newTurns <= 0) 0 else b.poisonDamage
            )
        }
        if (b.petrifyTurnsLeft > 0) {
            b = b.copy(petrifyTurnsLeft = b.petrifyTurnsLeft - 1)
        }
        return Pair(b, damageDealt)
    }

    fun advanceTurn(): BossState {
        return copy(turnCount = turnCount + 1)
    }

    companion object {
        fun forBoss(bossId: BossId): BossState {
            return BossState(
                id = bossId,
                name = bossId.displayName,
                maxHp = bossId.maxHp,
                currentHp = bossId.maxHp,
                minAttack = bossId.minAttack,
                maxAttack = bossId.maxAttack,
                backgroundRes = bossId.backgroundRes
            )
        }
    }
}
