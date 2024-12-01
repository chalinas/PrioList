package com.chalinas.priolist.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.chalinas.priolist.dao.TaskDao
import com.chalinas.priolist.database.TaskDatabase
import com.chalinas.priolist.models.Task
import com.chalinas.priolist.utils.Resource
import com.chalinas.priolist.utils.Resource.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskRepository(application: Application) {
    private val taskDao: TaskDao = TaskDatabase.getInstance(application).taskDao

    fun insertTask(task: Task) = MutableLiveData<Resource<Long>>().apply {
        postValue(Loading())
        try {
            CoroutineScope(Dispatchers.IO).launch{
                val result = taskDao.insertTask(task)
                postValue(Success(result))
            }
        } catch (e: Exception) {
            postValue(Error(e.message.toString()))

        }
    }
}