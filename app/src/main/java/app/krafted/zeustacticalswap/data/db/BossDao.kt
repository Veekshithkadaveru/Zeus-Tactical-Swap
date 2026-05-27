package app.krafted.zeustacticalswap.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface BossDao {

    @Query("SELECT * FROM boss_records")
    fun observeAll(): Flow<List<BossRecord>>

    @Query("SELECT * FROM boss_records WHERE bossId = :bossId")
    suspend fun getByBossId(bossId: String): BossRecord?

    @Upsert
    suspend fun upsert(record: BossRecord)
}
