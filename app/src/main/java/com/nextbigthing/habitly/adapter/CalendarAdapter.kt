package com.nextbigthing.habitly.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nextbigthing.habitly.room.data.CalendarDay
import com.nextbigthing.habitly.R

class CalendarAdapter(val days: List<CalendarDay>) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {


    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayText: TextView = itemView.findViewById(R.id.textView6)
        val dateText: TextView = itemView.findViewById(R.id.textView7)
        val cardView: CardView = itemView.findViewById(R.id.calendarCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_calender, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val item = days[position]
        holder.dayText.text = item.dayOfWeek
        holder.dateText.text = item.dayOfMonth

        if (item.isToday) {
            holder.cardView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, R.color.powder_red))
        } else {
            holder.cardView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, R.color.yellow))
        }
    }

    override fun getItemCount(): Int = days.size
}