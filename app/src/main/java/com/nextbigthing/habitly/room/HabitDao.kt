package com.nextbigthing.habitly.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface HabitDao {

    @Query("SELECT * FROM habit WHERE status= 0")
    suspend fun getPendingHabits(): List<Habit>

    @Query("SELECT * FROM habit WHERE status= 1")
    suspend fun getCompletedHabits(): List<Habit>

    @Query("SELECT * FROM habit WHERE uid = :habitId LIMIT 1")
    suspend fun getHabitById(habitId: Int): Habit?

    @Insert
    suspend fun insertAll(vararg habits: Habit)

    @Update
    suspend fun update(habit: Habit)

    @Delete
    suspend fun delete(habit: Habit)

    @Query("DELETE FROM habit")
    suspend fun deleteAll()
}