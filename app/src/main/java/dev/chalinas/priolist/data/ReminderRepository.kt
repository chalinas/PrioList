package dev.chalinas.priolist.data

import com.example.priolist.data.dao.ReminderDao
import com.example.priolist.data.model.Reminder
import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val reminderDao: ReminderDao) {
    fun getRemindersForTask(taskId: Int): Flow<List<Reminder>> = reminderDao.getRemindersForTask(taskId)

    suspend fun insert(reminder: Reminder) {
        reminderDao.insertReminder(reminder)
    }
}