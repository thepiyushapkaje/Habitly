package com.nextbigthing.habitly.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
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
            CoroutineScope(Dispatchers.IO).launch {
                val db = RoomHelper.getDatabase(this@DashboardActivity)
                val userDao = db.habitDao()

                // Insert a new habit
                userDao.insertAll(Habit(firstName = "Piyush", isCompleted = false))

                // Fetch updated list
                val updatedHabits = userDao.getAll()

                // Update UI on main thread
                withContext(Dispatchers.Main) {
                    todoAdapter.updateList(updatedHabits)
                }
            }
        }
    }
}
