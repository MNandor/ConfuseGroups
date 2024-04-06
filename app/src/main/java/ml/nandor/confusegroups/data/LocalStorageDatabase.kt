package ml.nandor.confusegroups.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.ConfuseGroup
import ml.nandor.confusegroups.domain.model.Deck
import ml.nandor.confusegroups.domain.model.GroupMembership
import ml.nandor.confusegroups.domain.model.ManualConfusion
import ml.nandor.confusegroups.domain.model.NewReview
import ml.nandor.confusegroups.domain.model.Review

@Database(entities = [Deck::class, AtomicNote::class, Review::class, ManualConfusion::class, ConfuseGroup::class, GroupMembership::class, NewReview::class], version = 11, exportSchema = false)
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
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11)
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

        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE Deck " +
                            "ADD COLUMN displayName TEXT;"
                )
            }
        }

        private val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE AtomicNote " +
                            "ADD COLUMN questionDisplay TEXT;"
                )
            }
        }

        private val MIGRATION_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "UPDATE AtomicNote " +
                            "SET questionDisplay = question;"
                )
            }
        }

        private val MIGRATION_5_6: Migration = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create ConfuseGroup table
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `ConfuseGroup` (" +
                            "`id` TEXT NOT NULL, " +
                            "`displayName` TEXT, " +
                            "PRIMARY KEY(`id`))"
                )
            }
        }

        private val MIGRATION_6_7: Migration = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create GroupMembership table
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `GroupMembership` (" +
                            "`id` TEXT NOT NULL, " +
                            "`cardID` TEXT NOT NULL, " +
                            "`groupID` TEXT NOT NULL, " +
                            "PRIMARY KEY(`id`))"
                )
            }
        }

        private val MIGRATION_7_8: Migration = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create NewReview table
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `NewReview` (" +
                            "`reviewID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`timeStamp` INTEGER NOT NULL, " +
                            "`questionID` TEXT NOT NULL, " +
                            "`answerOptionID` TEXT NOT NULL, " +
                            "`levelWhereThisHappened` INTEGER NOT NULL, " +
                            "`streakValueAfterThis` INTEGER NOT NULL, " +
                            "`wasThisOptionPicked` INTEGER NOT NULL)"
                )
            }
        }

        private val MIGRATION_8_9: Migration = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE NewReview")
                // Create NewReview table
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `NewReview` (" +
                            "`reviewID` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "`timeStamp` INTEGER NOT NULL, " +
                            "`questionID` TEXT NOT NULL, " +
                            "`answerOptionID` TEXT NOT NULL, " +
                            "`levelWhereThisHappened` INTEGER NOT NULL, " +
                            "`streakValueAfterThis` INTEGER NOT NULL, " +
                            "`wasThisOptionPicked` INTEGER NOT NULL)"
                )
            }
        }

        private val MIGRATION_9_10: Migration = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE AtomicNote " +
                            "ADD COLUMN mnemonic TEXT;"
                )
            }
        }

        private val MIGRATION_10_11: Migration = object : Migration(10, 11) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE NewReview " +
                            "ADD COLUMN deckID TEXT NOT NULL DEFAULT '';"
                )
            }
        }


    }
}