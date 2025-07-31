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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.nextbigthing.habitly.utils.AppCustom
import com.nextbigthing.habitly.ui.viewmodel.DashboardViewModel
import com.nextbigthing.habitly.R
import com.nextbigthing.habitly.adapter.CalendarAdapter
import com.nextbigthing.habitly.adapter.DoneAdapter
import com.nextbigthing.habitly.adapter.TodoAdapter
import com.nextbigthing.habitly.databinding.ActivityDashboardBinding
import kotlinx.coroutines.launch

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
        setupSwipeToDelete()
        handleClickEvents()

        // Observe StateFlows using lifecycleScope
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.pendingHabits.collect {
                    todoAdapter.updateList(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.completedHabits.collect {
                    doneAdapter.updateList(it)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.resetHabitsIfNewDay()
        viewModel.loadHabits()
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
        binding.dateRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.todoRecyclerView.adapter = todoAdapter
        binding.doneRecyclerView.adapter = doneAdapter
        binding.dateRecyclerView.adapter = CalendarAdapter(AppCustom.generateCalendarDays())
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

    private fun setupSwipeToDelete() {
        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                // Get the current list from adapter (you need to add this getter in adapter)
                val currentList = todoAdapter.getCurrentList().toMutableList()
                val deletedHabit = currentList[position]

                // Remove it from the list and update adapter
                currentList.removeAt(position)
                todoAdapter.updateList(currentList)

                // Delete from DB via ViewModel
                viewModel.deleteHabit(deletedHabit)

                // Optional Undo using Snackbar
                Snackbar.make(binding.todoRecyclerView, "Habit deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        viewModel.addHabit(deletedHabit.firstName.toString())
                    }.show()
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.todoRecyclerView)
    }
}
