package com.nextbigthing.habitly.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nextbigthing.habitly.DashboardViewModel
import com.nextbigthing.habitly.R
import com.nextbigthing.habitly.adapter.CalendarAdapter
import com.nextbigthing.habitly.adapter.DoneAdapter
import com.nextbigthing.habitly.adapter.TodoAdapter
import com.nextbigthing.habitly.databinding.ActivityDashboardBinding
import com.nextbigthing.habitly.room.Habit
import com.nextbigthing.habitly.room.RoomHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var doneAdapter: DoneAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupAdapters()
        setupRecyclerViews()
        handleClickEvents()

        // Observe StateFlows using lifecycleScope
        lifecycleScope.launch {
            viewModel.pendingHabits.collect {
                todoAdapter.updateList(it)
            }
        }

        lifecycleScope.launch {
            viewModel.completedHabits.collect {
                doneAdapter.updateList(it)
            }
        }
    }

    private fun setupAdapters() {
        todoAdapter = TodoAdapter(emptyList()) {
            viewModel.updateHabitCompletion(it, true)
        }
        doneAdapter = DoneAdapter(emptyList()) {
            viewModel.updateHabitCompletion(it, false)
        }
    }

    private fun setupRecyclerViews() {
        binding.todoRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.doneRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.dateRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.todoRecyclerView.adapter = todoAdapter
        binding.doneRecyclerView.adapter = doneAdapter
        binding.dateRecyclerView.adapter = CalendarAdapter()
    }

    private fun handleClickEvents() {
        binding.textView2.setOnClickListener {
            viewModel.deleteAll()
        }

        binding.fabAddHabit.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dailog_add_habit, null)
            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            val habitEditText = dialogView.findViewById<EditText>(R.id.habitEditText)
            val confirmHabitButton = dialogView.findViewById<Button>(R.id.confirmHabitButton)

            dialog.show()

            confirmHabitButton.setOnClickListener {
                val habitName = habitEditText.text.toString()
                if (habitName.isNotEmpty()) {
                    viewModel.addHabit(habitName)
                    dialog.dismiss()
                } else {
                    habitEditText.error = "Please enter a habit"
                }
            }
        }
    }
}
