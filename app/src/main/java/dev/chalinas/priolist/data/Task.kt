package dev.chalinas.priolist.data

import androidx.room.*
import java.time.LocalDateTime

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val taskId: Int = 0,
    val userId: Int,
    val categoryId: Int,
    val title: String,
    val description: String,
    val dueDate: LocalDateTime,
    val priority: Priority,
    val isCompleted: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)