package ml.nandor.confusegroups.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.Deck
import ml.nandor.confusegroups.domain.model.Review

@Database(entities = [Deck::class, AtomicNote::class, Review::class], version = 1, exportSchema = false)
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
                )
                .build()
                INSTANCE = instance

                instance
            }
        }
    }
}