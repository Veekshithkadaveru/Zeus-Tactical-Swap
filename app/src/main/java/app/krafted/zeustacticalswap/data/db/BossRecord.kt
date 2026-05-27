package app.krafted.zeustacticalswap.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "boss_records")
data class BossRecord(
    @PrimaryKey val bossId: String,
    val defeated: Boolean = false,
    val bestClearTimeMillis: Long? = null,
    val lastClearedAt: Long? = null
)
