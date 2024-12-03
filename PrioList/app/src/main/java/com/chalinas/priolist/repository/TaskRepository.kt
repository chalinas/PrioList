package com.chalinas.priolist.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chalinas.priolist.dao.TaskDao
import com.chalinas.priolist.database.TaskDatabase
import com.chalinas.priolist.models.Task
import com.chalinas.priolist.utils.Resource
import com.chalinas.priolist.utils.Resource.*
import com.chalinas.priolist.utils.StatusResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class TaskRepository(application: Application) {
    private val taskDao: TaskDao = TaskDatabase.getInstance(application).taskDao

    private val _taskStateFlow = MutableStateFlow<Resource<Flow<List<Task>>>>(Loading())
    val taskStateFlow  : StateFlow<Resource<Flow<List<Task>>>>
        get() = _taskStateFlow

    private val _statusLiveData = MutableLiveData<Resource<StatusResult>>()
    val statusLiveData : LiveData<Resource<StatusResult>>
        get() = _statusLiveData

    fun getTaskList()  {
        CoroutineScope(Dispatchers.IO).launch {
            _taskStateFlow.emit(Loading())
            try{
                val result = taskDao.getTaskList()
                _taskStateFlow.emit(Success("loading",result))
                delay(500)
            } catch (e: Exception) {
                _taskStateFlow.emit(Error(e.message.toString()))
            }
        }
    }

    fun insertTask(task: Task) {
        try {
        _statusLiveData.postValue(Loading())
            CoroutineScope(Dispatchers.IO).launch{
                val result = taskDao.insertTask(task)
                handleResult(result.toInt(),"Task added successfully",StatusResult.Added)
            }
        } catch (e: Exception) {
            _statusLiveData.postValue(Error(e.message.toString()))
            }
    }


    fun deleteTask(task: Task){
        try {
            _statusLiveData.postValue(Loading())
            CoroutineScope(Dispatchers.IO).launch{
                val result = taskDao.deleteTask(task)
                handleResult(result,"Task deleted successfully",StatusResult.Deleted)
            }
        } catch (e: Exception) {
            _statusLiveData.postValue(Error(e.message.toString()))
        }
    }

    fun updateTask(task: Task){
        try {
            _statusLiveData.postValue(Loading())
            CoroutineScope(Dispatchers.IO).launch{
                val result = taskDao.updateTask(task)
                handleResult(result,"Task updated successfully",StatusResult.Updated)
            }
        } catch (e: Exception) {
            _statusLiveData.postValue(Error(e.message.toString()))
        }
    }

    fun updateTaskParticularField(taskId: String, title: String, description: String){
        try {
            _statusLiveData.postValue(Loading())
            CoroutineScope(Dispatchers.IO).launch{
                val result = taskDao.updateTaskParticularField(taskId, title, description)
                handleResult(result,"Task updated successfully",StatusResult.Updated)
            }
        } catch (e: Exception) {
            _statusLiveData.postValue(Error(e.message.toString()))
        }
    }

private fun handleResult(result: Int,message: String, statusResult: StatusResult){
    if (result != -1){
        _statusLiveData.postValue(Success(message,statusResult))
    }else{
        _statusLiveData.postValue(Error("Something went wrong",statusResult))
    }
}
}