package app.krafted.zeustacticalswap.data.db

import app.krafted.zeustacticalswap.game.BossId
import kotlinx.coroutines.flow.Flow

class BossProgressRepository(private val dao: BossDao) {

    val allRecords: Flow<List<BossRecord>> = dao.observeAll()

    suspend fun markDefeated(bossId: BossId, clearTimeMillis: Long) {
        val existing = dao.getByBossId(bossId.name)
        val bestTime = when (val previous = existing?.bestClearTimeMillis) {
            null -> clearTimeMillis
            else -> minOf(previous, clearTimeMillis)
        }
        dao.upsert(
            BossRecord(
                bossId = bossId.name,
                defeated = true,
                bestClearTimeMillis = bestTime,
                lastClearedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun isDefeated(bossId: BossId): Boolean {
        return dao.getByBossId(bossId.name)?.defeated == true
    }

    suspend fun bestClearTime(bossId: BossId): Long? {
        return dao.getByBossId(bossId.name)?.bestClearTimeMillis
    }
}
