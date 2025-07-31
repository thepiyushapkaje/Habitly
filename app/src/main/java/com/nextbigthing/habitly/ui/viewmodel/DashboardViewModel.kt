package com.nextbigthing.habitly.ui.viewmodel

import android.app.Application
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nextbigthing.habitly.room.data.Habit
import com.nextbigthing.habitly.room.RoomHelper
import com.nextbigthing.habitly.room.data.AppMeta
import com.nextbigthing.habitly.room.data.HabitStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = RoomHelper.getDatabase(application).habitDao()

    private val _pendingHabits = MutableStateFlow<List<Habit>>(emptyList())
    val pendingHabits: StateFlow<List<Habit>> = _pendingHabits.asStateFlow()

    private val _completedHabits = MutableStateFlow<List<Habit>>(emptyList())
    val completedHabits: StateFlow<List<Habit>> = _completedHabits.asStateFlow()

    private val _selectedHabit = MutableStateFlow<Habit?>(null)
    val selectedHabit: StateFlow<Habit?> = _selectedHabit.asStateFlow()

    private val _habitStatuses = MutableStateFlow<List<HabitStatus>>(emptyList())
    val habitStatuses: StateFlow<List<HabitStatus>> = _habitStatuses

    private val _completedDates = MutableStateFlow<List<String>>(emptyList())
    val completedDates: StateFlow<List<String>> = _completedDates

    init {
        loadHabits()
    }

    fun loadHabits() = viewModelScope.launch {
        _pendingHabits.value = dao.getPendingHabits()
        _completedHabits.value = dao.getCompletedHabits()
    }

    fun addHabit(name: String) = viewModelScope.launch {
        dao.insertAll(Habit(firstName = name, isCompleted = false))
        loadHabits()
    }

    fun updateHabitCompletion(habit: Habit, completed: Boolean) = viewModelScope.launch {
        val date = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now().toString()
        } else {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        }

        // 1. Update main habit status
        dao.update(habit.apply { isCompleted = completed })

        // 2. Save to HabitStatus table
        dao.insertHabitStatus(
            HabitStatus(
                habitId = habit.uid,
                date = date,
                isCompleted = completed
            )
        )

        // 3. Refresh UI
        loadHabits()
    }

    fun loadHabitById(id: Int) = viewModelScope.launch {
        _selectedHabit.value = dao.getHabitById(id)
    }

    fun updateHabitName(habit: Habit, newName: String) = viewModelScope.launch {
        val updatedHabit = habit.copy(firstName = newName)
        dao.update(updatedHabit)

        // Force reload the updated habit from DB so StateFlow emits
        _selectedHabit.value = dao.getHabitById(updatedHabit.uid)

        // Optional: also update full habit lists
        loadHabits()
    }

    fun deleteHabit(habit: Habit) = viewModelScope.launch {
        dao.delete(habit)
    }

    fun deleteAll() = viewModelScope.launch {
        dao.deleteAll()
        loadHabits()
    }

    fun insertHabitStatus(habitId: Int, date: String, completed: Boolean) = viewModelScope.launch {
        val status = HabitStatus(habitId = habitId, date = date, isCompleted = completed)
        dao.insertHabitStatus(status)
    }

    fun loadStatusesForHabit(habitId: Int) = viewModelScope.launch {
        _habitStatuses.value = dao.getStatusesForHabit(habitId)
    }

    fun loadCompletedDates(habitId: Int) = viewModelScope.launch {
        _completedDates.value = dao.getCompletedDatesForHabit(habitId)
    }

    fun resetHabitsIfNewDay() = viewModelScope.launch {
        val today = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now().toString()
        } else {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        }

        val meta = dao.getAppMeta()
        if (meta?.lastResetDate != today) {
            dao.resetAllHabitStatus()
            dao.insertAppMeta(AppMeta(id = 1, lastResetDate = today))
            loadHabits()
        }
    }
}