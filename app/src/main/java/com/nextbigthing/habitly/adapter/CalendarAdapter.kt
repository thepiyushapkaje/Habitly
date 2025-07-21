package com.nextbigthing.habitly.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nextbigthing.habitly.R

class CalendarAdapter : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {


    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_calender, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
    }

    override fun getItemCount(): Int = 7
}