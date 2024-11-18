package dev.chalinas.priolist.data.local

import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val reminderDao: ReminderDao) {
    fun getRemindersForTask(taskId: Int): Flow<List<Reminder>> = reminderDao.getRemindersForTask(taskId)

    suspend fun insert(reminder: Reminder) {
        reminderDao.insertReminder(reminder)
    }
}