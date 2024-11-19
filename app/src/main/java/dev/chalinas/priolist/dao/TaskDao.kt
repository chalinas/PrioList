package dev.chalinas.priolist.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.chalinas.priolist.data.Task

@Dao
interface TaskDao {

    @Insert
    fun insertTask(task: Task): Long

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): List<Task>

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTaskById(id: Int): Task?

    @Update
    fun updateTask(task: Task): Int

    @Delete
    fun deleteTask(task: Task): Int
}