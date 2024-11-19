package dev.chalinas.priolist.service

import dev.chalinas.priolist.data.Reminder
import dev.chalinas.priolist.repository.ReminderRepository


class ReminderService(private val reminderRepository: ReminderRepository) {
    fun getAllReminders(): List<Reminder> {
        return reminderRepository.getAllReminders()
    }

    fun getReminderById(id: Int): Reminder? {
        return reminderRepository.getReminderById(id)
    }

    fun createReminder(reminder: Reminder): Int {
        return reminderRepository.createReminder(reminder)
    }

    fun updateReminder(reminder: Reminder): Int {
        return reminderRepository.updateReminder(reminder)
    }

    fun deleteReminder(id: Int): Int {
        return reminderRepository.deleteReminder(id)
    }
}