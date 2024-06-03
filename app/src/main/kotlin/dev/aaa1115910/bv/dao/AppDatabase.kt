package dev.aaa1115910.bv.dao

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import dev.aaa1115910.bv.BuildConfig
import dev.aaa1115910.bv.entity.db.SearchHistoryDB
import dev.aaa1115910.bv.entity.db.UserDB
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.Date
import java.util.concurrent.Executors

@Database(
    entities = [SearchHistoryDB::class, UserDB::class],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3)
    ]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun userDao(): UserDao

    companion object {
        private var instance: AppDatabase? = null
        private val logger = KotlinLogging.logger { }

        @Suppress("unused")
        fun reset() {
            instance = null
        }

        @Synchronized
        fun getDatabase(context: Context): AppDatabase {
            instance?.let { return it }
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "AppDatabase.db"
            )
                .setQueryCallback(object : QueryCallback {
                    override fun onQuery(sqlQuery: String, bindArgs: List<Any?>) {
                        if (BuildConfig.DEBUG) logger.info { "SQL Query: $sqlQuery SQL Args: $bindArgs" }
                    }
                }, Executors.newSingleThreadExecutor())
                .build()
                .apply { instance = this }
        }
    }
}

private object Converters {
    @TypeConverter
    fun timestampToDate(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}