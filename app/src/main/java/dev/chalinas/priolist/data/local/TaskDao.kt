package dev.chalinas.priolist.data.local

import androidx.room.*
import dev.chalinas.priolist.data.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY priority DESC")
    fun getPendingTasks(): Flow<List<Task>>

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}