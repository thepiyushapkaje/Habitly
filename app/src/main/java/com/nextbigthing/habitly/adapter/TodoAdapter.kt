package com.nextbigthing.habitly.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nextbigthing.habitly.R
import com.nextbigthing.habitly.room.Habit
import javax.security.auth.callback.Callback

class TodoAdapter(private var todoList: List<Habit>, private val callback: (Habit) -> Unit) :
    RecyclerView.Adapter<TodoAdapter.CalendarViewHolder>() {

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val todoTitleTextView: TextView = itemView.findViewById(R.id.todoTitleTextView)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_todo, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.todoTitleTextView.text = todoList[position].firstName
        holder.imageView.setOnClickListener {
            callback(todoList[position])
        }
    }

    override fun getItemCount(): Int = todoList.size

    fun updateList(newHabits: List<Habit>) {
        todoList = newHabits
        notifyDataSetChanged()
    }
}