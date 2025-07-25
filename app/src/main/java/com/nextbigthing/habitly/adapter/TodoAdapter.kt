package com.nextbigthing.habitly.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nextbigthing.habitly.R
import com.nextbigthing.habitly.databinding.ItemViewTodoBinding
import com.nextbigthing.habitly.room.Habit
import com.nextbigthing.habitly.ui.HabitProgressActivity

class TodoAdapter(
    private var todoList: List<Habit>,
    private val callback: (Habit) -> Unit
) : RecyclerView.Adapter<TodoAdapter.CalendarViewHolder>() {

    inner class CalendarViewHolder(val binding: ItemViewTodoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = ItemViewTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val habit = todoList[position]
        val context = holder.itemView.context

        holder.binding.todoTitleTextView.text = habit.firstName

        holder.binding.todoTitleTextView.setOnClickListener {
            context.startActivity(Intent(context, HabitProgressActivity::class.java).apply {
                putExtra("title", habit.firstName)
            })
        }

        holder.binding.imageView.setImageResource(R.drawable.ic_checked)
        holder.binding.imageView.setOnClickListener {
            callback(habit)
        }
    }

    override fun getItemCount(): Int = todoList.size

    fun updateList(newHabits: List<Habit>) {
        todoList = newHabits
        notifyDataSetChanged()
    }

    fun getCurrentList(): List<Habit> = todoList
}