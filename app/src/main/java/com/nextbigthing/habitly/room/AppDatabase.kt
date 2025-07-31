package com.nextbigthing.habitly.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nextbigthing.habitly.room.data.AppMeta
import com.nextbigthing.habitly.room.data.Habit
import com.nextbigthing.habitly.room.data.HabitStatus

@Database(entities = [Habit::class, HabitStatus::class, AppMeta::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS AppMeta (
                id INTEGER NOT NULL PRIMARY KEY,
                lastResetDate TEXT NOT NULL
            )
            """.trimIndent()
        )
    }
}