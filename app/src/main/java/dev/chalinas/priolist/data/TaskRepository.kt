package dev.chalinas.priolist.data

import dev.chalinas.priolist.data.TaskDao
import dev.chalinas.priolist.data.Task
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    // Flujo de todas las tareas pendientes que se pueden observar
    val allPendingTasks: Flow<List<Task>> = taskDao.getPendingTasks()

    // Inserta una nueva tarea en la base de datos
    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
    }

    // Actualiza una tarea existente en la base de datos
    suspend fun update(task: Task) {
        taskDao.updateTask(task)
    }

    // Elimina una tarea de la base de datos
    suspend fun delete(task: Task) {
        taskDao.deleteTask(task)
    }
}