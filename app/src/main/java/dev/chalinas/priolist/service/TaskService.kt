package dev.chalinas.priolist.service

import dev.chalinas.priolist.data.Task
import dev.chalinas.priolist.repository.TaskRepository


class TaskService(private val taskRepository: TaskRepository) {
    fun getAllTasks(): List<Task> {
        return taskRepository.getAllTasks()
    }

    fun getTaskById(id: Int): Task? {
        return taskRepository.getTaskById(id)
    }

    fun createTask(task: Task): Int {
        return taskRepository.createTask(task)
    }

    fun updateTask(task: Task): Int {
        return taskRepository.updateTask(task)
    }

    fun deleteTask(id: Int): Int {
        return taskRepository.deleteTask(id)
    }
}



