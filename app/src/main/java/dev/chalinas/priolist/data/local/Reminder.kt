package dev.chalinas.priolist.data.local

import androidx.room.*
import java.time.LocalDateTime

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val reminderId: Int = 0,
    val taskId: Int,
    val remindAt: LocalDateTime,
    val isNotified: Boolean = false
)