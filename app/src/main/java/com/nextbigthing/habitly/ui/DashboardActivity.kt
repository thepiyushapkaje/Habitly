package com.nextbigthing.habitly.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
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
    private lateinit var todoAdapter: TodoAdapter

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

        binding.textView2.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val db = RoomHelper.getDatabase(this@DashboardActivity)
                val userDao = db.habitDao()
                // Insert a new habit
                userDao.deleteAll()
                val updatedHabits = userDao.getAll()

                // Update UI on main thread
                withContext(Dispatchers.Main) {
                    todoAdapter.updateList(updatedHabits)
                }
            }
        }

        binding.dateRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.dateRecyclerView.adapter = CalendarAdapter()

        binding.todoRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Load habits in background and set adapter on the main thread
        CoroutineScope(Dispatchers.IO).launch {
            val db = RoomHelper.getDatabase(this@DashboardActivity)
            val userDao = db.habitDao()
            val habits = userDao.getAll()

            withContext(Dispatchers.Main) {
                todoAdapter = TodoAdapter(habits)
                binding.todoRecyclerView.adapter = todoAdapter
            }
        }

        binding.doneRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.doneRecyclerView.adapter = DoneAdapter()

        binding.fabAddHabit.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dailog_add_habit, null)  // Inflate the layout
            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            val habitEditText = dialogView.findViewById<EditText>(R.id.habitEditText)
            val confirmHabitButton = dialogView.findViewById<Button>(R.id.confirmHabitButton)

            dialog.show()

            confirmHabitButton.setOnClickListener {
                if (habitEditText.text.isNotEmpty()) {
                    dialog.dismiss()
                    CoroutineScope(Dispatchers.IO).launch {
                        val db = RoomHelper.getDatabase(this@DashboardActivity)
                        val userDao = db.habitDao()

                        // Insert a new habit
                        userDao.insertAll(Habit(firstName = habitEditText.text.toString(), isCompleted = false))

                        // Fetch updated list
                        val updatedHabits = userDao.getAll()

                        // Update UI on main thread
                        withContext(Dispatchers.Main) {
                            todoAdapter.updateList(updatedHabits)
                        }
                    }
                }else{
                    habitEditText.error = "Please enter a habit"
                }
            }
        }
    }
}
