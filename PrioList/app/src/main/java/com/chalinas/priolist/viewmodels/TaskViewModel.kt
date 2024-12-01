package com.chalinas.priolist.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.chalinas.priolist.models.Task
import com.chalinas.priolist.repository.TaskRepository
import com.chalinas.priolist.utils.Resource

class TaskViewModel (application: Application) : AndroidViewModel(application){

    private val taskRepository = TaskRepository(application)

    fun insertTask(task: Task): MutableLiveData<Resource<Long>> {
        return taskRepository.insertTask(task)
    }
}