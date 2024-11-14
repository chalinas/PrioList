package dev.chalinas.priolist.data;

import androidx.room.*
import java.util.List;
import dev.chalinas.priolist.data.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder)

    @Query("SELECT * FROM reminders WHERE taskId = :taskId")
    fun getRemindersForTask(taskId: Int): Flow<List<Reminder>>
}
