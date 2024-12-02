package com.chalinas.priolist.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chalinas.priolist.models.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM Task ORDER BY date DESC")
    fun getTaskList() : Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Delete
    suspend fun deleteTask(task: Task) : Int
}