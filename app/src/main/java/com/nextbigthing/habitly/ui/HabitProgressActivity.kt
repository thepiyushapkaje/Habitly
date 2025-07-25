package com.nextbigthing.habitly.ui

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.nextbigthing.habitly.DashboardViewModel
import com.nextbigthing.habitly.R
import com.nextbigthing.habitly.databinding.ActivityHabitProgressBinding
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

        val dialogView = layoutInflater.inflate(R.layout.dailog_add_habit, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val addNewHabitLabel = dialogView.findViewById<TextView>(R.id.addNewHabitLabel)
        val habitEditText = dialogView.findViewById<EditText>(R.id.habitEditText)
        val confirmHabitButton = dialogView.findViewById<Button>(R.id.confirmHabitButton)

        val uniqueId = intent.getIntExtra("uniqueId", 0)
        viewModel.loadHabitById(uniqueId)
        lifecycleScope.launch {
            viewModel.selectedHabit.collect { habit ->
                addNewHabitLabel.text = "Edit Current Habit"
                confirmHabitButton.text = "Update Habit"
                habitEditText.setText("${habit?.firstName}")
                binding.todoTitleTextView.text = "${habit?.firstName}"
                confirmHabitButton.setOnClickListener {
                    habit?.let { it1 ->
                        viewModel.updateHabitName(
                            it1,
                            habitEditText.text.toString()
                        )
                    }
                    dialog.dismiss()
                }
            }
        }
        binding.fabAddHabit2.setOnClickListener {
            dialog.show()
        }
    }
}