package dev.chalinas.priolist.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.chalinas.priolist.data.Reminder

@Dao
interface ReminderDao {

    @Insert
    fun insertReminder(reminder: Reminder): Long

    @Query("SELECT * FROM reminders")
    fun getAllReminders(): List<Reminder>

    @Query("SELECT * FROM reminders WHERE id = :id")
    fun getReminderById(id: Int): Reminder?

    @Update
    fun updateReminder(reminder: Reminder): Int

    @Delete
    fun deleteReminder(reminder: Reminder): Int
}