package dev.chalinas.priolist.data.local

import androidx.room.*
import java.util.List;
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder)

    @Query("SELECT * FROM reminders WHERE taskId = :taskId")
    fun getRemindersForTask(taskId: Int): Flow<List<Reminder>>
}