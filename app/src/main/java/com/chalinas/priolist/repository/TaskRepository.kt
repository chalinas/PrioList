package com.chalinas.priolist.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chalinas.priolist.dao.ReminderDao
import com.chalinas.priolist.dao.TaskDao
import com.chalinas.priolist.database.TaskDatabase
import com.chalinas.priolist.models.Category
import com.chalinas.priolist.models.Reminder
import com.chalinas.priolist.models.Task
import com.chalinas.priolist.utils.AppAlarmManager
import com.chalinas.priolist.utils.Resource
import com.chalinas.priolist.utils.Resource.Error
import com.chalinas.priolist.utils.Resource.Loading
import com.chalinas.priolist.utils.Resource.Success
import com.chalinas.priolist.utils.StatusResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID


class TaskRepository(
    private val application: Application
) {

    private val taskDao = TaskDatabase.getInstance(application).taskDao
    private val categoryDao = TaskDatabase.getInstance(application).categoryDao
    private val reminderDao = TaskDatabase.getInstance(application).reminderDao


    private val _taskStateFlow = MutableStateFlow<Resource<Flow<List<Task>>>>(Loading())
    val taskStateFlow: StateFlow<Resource<Flow<List<Task>>>>
        get() = _taskStateFlow

    private val _statusLiveData = MutableLiveData<Resource<StatusResult>>()
    val statusLiveData: LiveData<Resource<StatusResult>>
        get() = _statusLiveData

    private val _sortByLiveData = MutableLiveData<Pair<String, Boolean>>().apply {
        postValue(Pair("title", true)) // Default: sort by title in ascending order
    }
    val sortByLiveData: LiveData<Pair<String, Boolean>> = _sortByLiveData

    fun setSortBy(sort: Pair<String, Boolean>) {
        _sortByLiveData.postValue(sort)
    }

    fun getTaskList(isAsc: Boolean, sortByCategory: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _taskStateFlow.emit(Loading())
            try {
                val result = when (sortByCategory) {
                    "category" -> taskDao.getTaskListSortByCategory(isAsc)
                    "priority" -> taskDao.getTaskListSortByPriority(isAsc)
                    else -> taskDao.getTaskListSortByTaskDate(isAsc)
                }
                _taskStateFlow.emit(Success("Tasks loaded", result))
            } catch (e: Exception) {
                _taskStateFlow.emit(Error(e.message.orEmpty()))
            }
        }
    }

    suspend fun insertOrUpdateTask(
        title: String,
        description: String,
        priority: Int,
        date: Date,
        category: String, // Category name
        reminderTime: Long // Reminder time
    ) {
        withContext(Dispatchers.IO) {
            try {
                // Retrieve or insert category
                val categoryId = getCategoryOrInsert(category)

                // Retrieve or insert reminder
                val reminderId = getReminderOrInsert(reminderTime)

                // Insert the task
                val task = Task(
                    id = generateTaskId(), // Ensure this is unique for new tasks
                    title = title,
                    description = description,
                    date = date,
                    categoryId = categoryId,
                    reminderId = reminderId,
                    priority = priority,
                    dueTime = reminderTime
                )
                insertTask(task)
//                taskDao.insertTask(task)
            } catch (e: Exception) {
                Log.e("TaskRepository", "Error inserting task: ${e.message}", e)
            }
        }
    }

    private suspend fun getCategoryOrInsert(categoryName: String): Int {
        val existingCategory = categoryDao.getCategoryByName(categoryName)
        return if (existingCategory != null) {
            existingCategory.categoryId.toInt()
        } else {
            val newCategory = Category(name = categoryName)
            categoryDao.insertCategory(newCategory).toInt() // Cast Long to Int
        }
    }

    private suspend fun getReminderOrInsert(reminderTime: Long): Int {
        val existingReminder = reminderDao.getReminderByTime(reminderTime)
        return if (existingReminder != null) {
            existingReminder.reminderId.toInt()
        } else {
            val newReminder = Reminder(time = reminderTime)
            reminderDao.insertReminder(newReminder).toInt() // Cast Long to Int
        }
    }

    private fun generateTaskId(): String {
        return UUID.randomUUID().toString()
    }

    // Insert or update category
    private suspend fun insertOrUpdateCategory(category: Category): Int {
        return categoryDao.insertCategory(category).toInt()
    }

    // Insert or update reminder
    private suspend fun insertOrUpdateReminder(reminder: Reminder): Int {
        return reminderDao.insertReminder(reminder).toInt()
    }

    // Update a task
    suspend fun updateTaskNew(
        taskId: String,
        title: String,
        description: String,
        priority: Int,
        category: String,
        reminderTime: Long,
        date: Date // Pass the updated date explicitly
    ) {
        withContext(Dispatchers.IO) {
            try {
                // Get or insert category
                val categoryId = getCategoryOrInsert(category)

                // Get or insert reminder
                val reminderId = getReminderOrInsert(reminderTime)

                // Create updated task object
                val updatedTask = Task(
                    id = taskId,
                    title = title,
                    description = description,
                    date = date,
                    categoryId = categoryId,
                    reminderId = reminderId,
                    priority = priority,
                    dueTime = reminderTime
                )

                // Update the task in the database
                val rowsUpdated = taskDao.updateTask(updatedTask)

                if (rowsUpdated > 0) {
                    // Schedule the updated task alarm
                    AppAlarmManager.scheduleTask(application, updatedTask)

                    // Notify UI of successful update
                    withContext(Dispatchers.Main) {
                        _statusLiveData.postValue(Success("Task updated successfully", StatusResult.Updated))
                    }
                } else {
                    throw Exception("Failed to update task")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _statusLiveData.postValue(Error("Error updating task: ${e.message.orEmpty()}"))
                }
            }
        }
    }


    fun insertTask(task: Task) {
        Log.e("TAG", "insertTask: repo ${task.id}")
        CoroutineScope(Dispatchers.IO).launch {
            _statusLiveData.postValue(Loading())
            try {
                val result = taskDao.insertTask(task)
                if (result != -1L) {
                    AppAlarmManager.scheduleTask(application, task) // Ensure alarms are scheduled
                    handleResult(result.toInt(), "Task added successfully", StatusResult.Added)
                } else {
                    _statusLiveData.postValue(Error("Failed to insert task"))
                }
            } catch (e: Exception) {
                _statusLiveData.postValue(Error(e.message.orEmpty()))
            }
        }
    }

    fun deleteTask(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            _statusLiveData.postValue(Loading())
            try {
                val result = taskDao.deleteTask(task)
                if (result > 0) {
                    AppAlarmManager.cancelTask(application, task) // Cancel alarms for this task
                    handleResult(result, "Task deleted successfully", StatusResult.Deleted)
                } else {
                    _statusLiveData.postValue(Error("Failed to delete task"))
                }
            } catch (e: Exception) {
                _statusLiveData.postValue(Error(e.message.orEmpty()))
            }
        }
    }

    fun updateTask(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            _statusLiveData.postValue(Loading())
            try {
                val result = taskDao.updateTask(task)
                if (result > 0) {
                    AppAlarmManager.scheduleTask(application, task) // Update alarms
                    handleResult(result, "Task updated successfully", StatusResult.Updated)
                } else {
                    _statusLiveData.postValue(Error("Failed to update task"))
                }
            } catch (e: Exception) {
                _statusLiveData.postValue(Error(e.message.orEmpty()))
            }
        }
    }

    fun searchTask(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _taskStateFlow.emit(Loading())
            try {
                val result = taskDao.searchTaskList("%$query%")
                _taskStateFlow.emit(Success("Search results", result))
            } catch (e: Exception) {
                _taskStateFlow.emit(Error(e.message.orEmpty()))
            }
        }
    }

    fun deleteTaskUsingId(taskId: String) {
        try {
            _statusLiveData.postValue(Loading())
            CoroutineScope(Dispatchers.IO).launch {
                val result = taskDao.deleteTaskUsingId(taskId)
                handleResult(result, "Deleted Task Successfully", StatusResult.Deleted)

            }
        } catch (e: Exception) {
            _statusLiveData.postValue(Error(e.message.toString()))
        }
    }

    private fun handleResult(result: Int, message: String, statusResult: StatusResult) {
        if (result > 0) {
            _statusLiveData.postValue(Success(message, statusResult))
        } else {
            _statusLiveData.postValue(Error("Operation failed"))
        }
    }
}
