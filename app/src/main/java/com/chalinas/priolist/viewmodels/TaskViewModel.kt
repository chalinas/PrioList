package com.chalinas.priolist.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.room.Query
import com.chalinas.priolist.models.Task
import com.chalinas.priolist.repository.TaskRepository
import com.chalinas.priolist.utils.Resource
import java.util.Date

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val taskRepository = TaskRepository(application)
    val taskStateFlow get() =  taskRepository.taskStateFlow
    val statusLiveData get() =  taskRepository.statusLiveData
    val sortByLiveData get() =  taskRepository.sortByLiveData

    fun setSortBy(sort:Pair<String,Boolean>){
        taskRepository.setSortBy(sort)
    }

    fun getTaskList(isAsc : Boolean, sortByCategory:String) {
        taskRepository.getTaskList(isAsc, sortByCategory)
    }

    suspend fun insertOrUpdateTask(
        title: String,
        description: String,
        priority: Int,
        date: Date,
        category: String,  // Category name
        reminderTime: Long // Reminder time
    ){
        Log.e("TAG", "insertOrUpdateTask:viewmodel ", )
        taskRepository.insertOrUpdateTask(title,description,priority,date,category,reminderTime)
    }
    suspend fun updateTask(
        taskId: String,
        title: String,
        description: String,
        priority: Int,
        category: String,
        reminderTime: Long
    ){
        taskRepository.updateTaskNew(taskId,title,description,priority,category,reminderTime,Date())
    }
    fun insertTask(task: Task){
        taskRepository.insertTask(task)
    }

    fun deleteTask(task: Task) {
        taskRepository.deleteTask(task)
    }

    fun deleteTaskUsingId(taskId: String){
        taskRepository.deleteTaskUsingId(taskId)
    }

    fun updateTask(task: Task) {
        taskRepository.updateTask(task)
    }

    fun searchTaskList(query: String){
        taskRepository.searchTask(query)
    }
}