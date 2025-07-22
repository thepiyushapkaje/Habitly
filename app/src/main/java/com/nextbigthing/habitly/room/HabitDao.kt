package com.nextbigthing.habitly.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HabitDao {

    @Query("SELECT * FROM habit")
    fun getAll(): List<Habit>

    @Query("SELECT * FROM habit WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Habit>

    @Insert
    fun insertAll(vararg habit: Habit)

    @Delete
    fun delete(habit: Habit)
}