package app.krafted.zeustacticalswap.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BossRecord::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bossDao(): BossDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "zeus.db"
                ).build().also { instance = it }
            }
        }
    }
}
