package dev.chalinas.priolist.repository

import dev.chalinas.priolist.data.Task


interface TaskRepository {
    fun getAllTasks(): List<Task>
    fun getTaskById(id: Int): Task?
    fun createTask(task: Task): Int
    fun updateTask(task: Task): Int
    fun deleteTask(id: Int): Int
}



