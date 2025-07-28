package com.nextbigthing.habitly

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nextbigthing.habitly.room.Habit
import com.nextbigthing.habitly.room.RoomHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = RoomHelper.getDatabase(application).habitDao()

    private val _pendingHabits = MutableStateFlow<List<Habit>>(emptyList())
    val pendingHabits: StateFlow<List<Habit>> = _pendingHabits.asStateFlow()

    private val _completedHabits = MutableStateFlow<List<Habit>>(emptyList())
    val completedHabits: StateFlow<List<Habit>> = _completedHabits.asStateFlow()

    private val _selectedHabit = MutableStateFlow<Habit?>(null)
    val selectedHabit: StateFlow<Habit?> = _selectedHabit.asStateFlow()

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
        dao.update(habit.apply { isCompleted = completed })
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
}