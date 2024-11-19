package dev.chalinas.priolist.repository

import dev.chalinas.priolist.data.Reminder


interface ReminderRepository {
    fun getAllReminders(): List<Reminder>
    fun getReminderById(id: Int): Reminder?
    fun createReminder(reminder: Reminder): Int
    fun updateReminder(reminder: Reminder): Int
    fun deleteReminder(id: Int): Int
}