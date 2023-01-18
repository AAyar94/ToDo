package com.aayar94.todo.data

import androidx.room.TypeConverter
import com.aayar94.todo.data.models.Priority

class Converter {
    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }

    @TypeConverter
    fun fromString(priority: String): Priority {
        return Priority.valueOf(priority)
    }

}