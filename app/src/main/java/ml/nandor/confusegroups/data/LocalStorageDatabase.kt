package ml.nandor.confusegroups.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.Deck
import ml.nandor.confusegroups.domain.model.ManualConfusion
import ml.nandor.confusegroups.domain.model.Review

@Database(entities = [Deck::class, AtomicNote::class, Review::class, ManualConfusion::class], version = 2, exportSchema = false)
abstract class LocalStorageDatabase: RoomDatabase() {
    abstract fun dao(): DataAccessObject

    companion object{
        @Volatile
        private var INSTANCE: LocalStorageDatabase? = null

        fun getDatabase(context: Context): LocalStorageDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalStorageDatabase::class.java,
                    "local_database"
                ).addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance

                instance
            }
        }

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS ManualConfusion " +
                            "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "leftCard TEXT NOT NULL, " +
                            "rightCard TEXT NOT NULL)")
            }
        }
    }
}