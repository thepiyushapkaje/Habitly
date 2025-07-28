package com.nextbigthing.habitly.ui

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.nextbigthing.habitly.DashboardViewModel
import com.nextbigthing.habitly.R
import com.nextbigthing.habitly.databinding.ActivityHabitProgressBinding
import com.nextbigthing.habitly.room.Habit
import kotlinx.coroutines.launch
import kotlin.getValue


class HabitProgressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHabitProgressBinding
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHabitProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inflate dialog and get views
        val dialogView = layoutInflater.inflate(R.layout.dailog_add_habit, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val addNewHabitLabel = dialogView.findViewById<TextView>(R.id.addNewHabitLabel)
        val habitEditText = dialogView.findViewById<EditText>(R.id.habitEditText)
        val confirmHabitButton = dialogView.findViewById<Button>(R.id.confirmHabitButton)

        // Get habit ID from Intent
        val uniqueId = intent.getIntExtra("uniqueId", 0)

        // Load habit initially
        viewModel.loadHabitById(uniqueId)

        var currentHabit: Habit? = null

        // Observe selected habit
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedHabit.collect { habit ->
                    currentHabit = habit
                    habit?.let {
                        addNewHabitLabel.text = "Edit Current Habit"
                        confirmHabitButton.text = "Update Habit"
                        habitEditText.setText(it.firstName)
                        binding.todoTitleTextView.text = it.firstName
                    }
                }
            }
        }

        // Handle update button click
        confirmHabitButton.setOnClickListener {
            val updatedName = habitEditText.text.toString().trim()

            if (updatedName.isNotEmpty()) {
                currentHabit?.let { oldHabit ->
                    viewModel.updateHabitName(oldHabit, updatedName)

                    // Reload the updated habit to refresh UI
                    viewModel.loadHabitById(oldHabit.uid)

                    Toast.makeText(this, "Habit updated", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            } else {
                habitEditText.error = "Please enter a valid name"
            }
        }

        // Show dialog
        binding.fabAddHabit2.setOnClickListener {
            dialog.show()
        }
    }
}
