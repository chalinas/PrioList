package com.chalinas.priolist.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val reminderId: Long = 0,
    val time: Long // Timestamp for the reminder
)
