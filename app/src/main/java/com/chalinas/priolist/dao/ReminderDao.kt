package com.chalinas.priolist.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chalinas.priolist.models.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder):Long

    @Query("SELECT * FROM reminders WHERE reminderId = :id")
    suspend fun getReminderById(id: Int): Reminder?

    @Query("SELECT * FROM reminders ORDER BY time ASC") // Optional: You could retrieve all reminders in a specific order.
    fun getAllReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE time = :time LIMIT 1")
    suspend fun getReminderByTime(time: Long): Reminder?

}

