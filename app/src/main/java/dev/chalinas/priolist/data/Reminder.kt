package dev.chalinas.priolist.data


data class Reminder(
    val id: Int,
    val taskId: Int,
    val reminderTime: Date,
    val reminderType: ReminderType
)
