package com.chalinas.priolist.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.chalinas.priolist.models.Task
import com.chalinas.priolist.repository.TaskRepository
import com.chalinas.priolist.utils.Resource


class TaskViewModel (application: Application) : AndroidViewModel(application){

    private val taskRepository = TaskRepository(application)
    val taskStateFlow get() = taskRepository.taskStateFlow
    val statusLiveData get() = taskRepository.statusLiveData

    fun getTaskList() {
        taskRepository.getTaskList()
    }

    fun insertTask(task: Task){
        taskRepository.insertTask(task)
    }

    fun deleteTask(task: Task){
        taskRepository.deleteTask(task)
    }

    fun updateTask(task: Task){
        taskRepository.updateTask(task)
    }

    fun updateTaskParticularField(taskId: String, title: String, description: String){
        taskRepository.updateTaskParticularField(taskId, title, description)
    }
}