package com.nextbigthing.habitly

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object AppCustom {

    internal fun generateCalendarDays(): List<CalendarDay> {
        val calendar = Calendar.getInstance()
        val today = calendar.clone() as Calendar

        val result = mutableListOf<CalendarDay>()

        calendar.add(Calendar.DAY_OF_MONTH, -2) // Go 2 days back

        for (i in 0 until 5) {
            val dayOfWeek = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time) // Mon, Tue...
            val dayOfMonth = SimpleDateFormat("dd", Locale.getDefault()).format(calendar.time) // 01, 02...

            val isToday = calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)

            result.add(CalendarDay(dayOfWeek, dayOfMonth, isToday))

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return result
    }
}