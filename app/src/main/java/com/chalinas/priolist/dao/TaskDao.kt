package com.chalinas.priolist.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.chalinas.priolist.models.Task
import com.chalinas.priolist.models.TaskWithDetails
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {

    @Query(
        """
        SELECT * FROM Task ORDER BY
        CASE WHEN :isAsc = 1 THEN categoryId END ASC, 
        CASE WHEN :isAsc = 0 THEN categoryId END DESC
    """
    )
    fun getTaskListSortByCategory(isAsc: Boolean): Flow<List<Task>>

    @Query(
        """
        SELECT * FROM Task ORDER BY
        CASE WHEN :isAsc = 1 THEN date END ASC, 
        CASE WHEN :isAsc = 0 THEN date END DESC
    """
    )
    fun getTaskListSortByTaskDate(isAsc: Boolean): Flow<List<Task>>

    @Query(
        """
        SELECT * FROM Task ORDER BY
        CASE WHEN :isAsc = 1 THEN priority END ASC, 
        CASE WHEN :isAsc = 0 THEN priority END DESC
    """
    )
    fun getTaskListSortByPriority(isAsc: Boolean): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Delete
    suspend fun deleteTask(task: Task): Int

    @Query("DELETE FROM Task WHERE taskId = :taskId")
    suspend fun deleteTaskUsingId(taskId: String): Int

    @Update
    suspend fun updateTask(task: Task): Int


    @Query(
        """
        UPDATE Task SET taskTitle = :title, description = :description, priority = :priority 
        WHERE taskId = :taskId
    """
    )
    suspend fun updateTaskParticularFields(
        taskId: String,
        title: String,
        description: String,
        priority: Int
    ): Int

    @Query(
        """
        SELECT * FROM Task WHERE taskTitle LIKE :query 
        ORDER BY date DESC
    """
    )
    fun searchTaskList(query: String): Flow<List<Task>>

    @Query("SELECT * FROM Task")
    fun getAllTasks(): Flow<List<Task>>


    @Query(
        """
    SELECT t.*, 
           c.name AS categoryName, 
           r.time AS reminderTime 
    FROM Task t
    LEFT JOIN CATEGORIES c ON t.categoryId = c.categoryId
    LEFT JOIN REMINDERS r ON t.reminderId = r.reminderId
    """
    )
    fun getTasksWithDetails(): Flow<List<TaskWithDetails>>

}

