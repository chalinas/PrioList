package com.chalinas.priolist

import android.app.AlarmManager
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.view.ViewCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chalinas.priolist.adapters.TaskRVVBListAdapter
import com.chalinas.priolist.databinding.ActivityMainBinding
import com.chalinas.priolist.models.Task
import com.chalinas.priolist.utils.AppNotificationManager
import com.chalinas.priolist.utils.CategoryPreferences
import com.chalinas.priolist.utils.Status
import com.chalinas.priolist.utils.StatusResult
import com.chalinas.priolist.utils.StatusResult.Added
import com.chalinas.priolist.utils.StatusResult.Deleted
import com.chalinas.priolist.utils.StatusResult.Updated
import com.chalinas.priolist.utils.clearEditText
import com.chalinas.priolist.utils.hideKeyBoard
import com.chalinas.priolist.utils.longToastShow
import com.chalinas.priolist.utils.setupDialog
import com.chalinas.priolist.utils.showTimePicker
import com.chalinas.priolist.utils.validateEditText
import com.chalinas.priolist.viewmodels.TaskViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


class MainActivity : AppCompatActivity() {
    private val shouldShowNotifRationale = true
    private lateinit var categoryPreferences: CategoryPreferences

    private val mainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val addTaskDialog: Dialog by lazy {
        Dialog(this, R.style.DialogCustomTheme).apply {
            setupDialog(R.layout.add_task_dialog)
        }
    }

    private val updateTaskDialog: Dialog by lazy {
        Dialog(this, R.style.DialogCustomTheme).apply {
            setupDialog(R.layout.update_task_dialog)
        }
    }

    private val loadingDialog: Dialog by lazy {
        Dialog(this, R.style.DialogCustomTheme).apply {
            setupDialog(R.layout.loading_dialog)
        }
    }

    private val taskViewModel: TaskViewModel by lazy {
        ViewModelProvider(this)[TaskViewModel::class.java]
    }

    private val isListMutableLiveData = MutableLiveData<Boolean>().apply {
        postValue(true)
    }

    private var selectedDueTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)
        categoryPreferences = CategoryPreferences(this)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && notificationManager.areNotificationsEnabled() && isNotifPermEnabled()) {
            AppNotificationManager.createNotificationChannel(this)
        } else {
            AppNotificationManager.createNotificationChannel(this)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !notificationManager.areNotificationsEnabled() && !isNotifPermEnabled()) {
                if (!shouldShowNotifRationale) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val settingsIntent: Intent =
                            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK
                            ).putExtra(
                                Settings.EXTRA_APP_PACKAGE,
                                packageName
                            )
                        startActivity(settingsIntent)
                    }
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf<String>(android.Manifest.permission.POST_NOTIFICATIONS),
                        890
                    )
//                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                }
            }
        }

        val categories = listOf("Work", "Personal", "Shopping", "Others")

        val addTaskPrioritySpinner = addTaskDialog.findViewById<Spinner>(R.id.spinnerPriority)
        val updateTaskPrioritySpinner = updateTaskDialog.findViewById<Spinner>(R.id.spinnerPriority)

        val priorities = listOf("High", "Normal", "Low")
        val priorityMap = mapOf("High" to 3, "Normal" to 2, "Low" to 1)

        // Add task start
        val addCloseImg = addTaskDialog.findViewById<ImageView>(R.id.closeImg)
        addCloseImg.setOnClickListener { addTaskDialog.dismiss() }

        val addETTitle = addTaskDialog.findViewById<TextInputEditText>(R.id.edTaskTitle)
        val addETTitleL = addTaskDialog.findViewById<TextInputLayout>(R.id.edTaskTitleL)

        addETTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(addETTitle, addETTitleL)
            }

        })

        val addETDesc = addTaskDialog.findViewById<TextInputEditText>(R.id.edTaskDesc)
        val addETDescL = addTaskDialog.findViewById<TextInputLayout>(R.id.edTaskDescL)

        addETDesc.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(addETDesc, addETDescL)
            }
        })

        mainBinding.addTaskFABtn.setOnClickListener {
            clearEditText(addETTitle, addETTitleL)
            clearEditText(addETDesc, addETDescL)
            addTaskDialog.show()
        }
        val saveTaskBtn = addTaskDialog.findViewById<Button>(R.id.saveTaskBtn)

        val addTaskCategoryDropdown =
            addTaskDialog.findViewById<AutoCompleteTextView>(R.id.taskCategoryDropdown)
        val addCategoryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            categoryPreferences.getCategories()
        )
        addTaskCategoryDropdown.setAdapter(addCategoryAdapter)

        addTaskCategoryDropdown.setOnItemClickListener { _, _, position, _ ->
            val selectedCategory = addTaskCategoryDropdown.text.toString()
            if (selectedCategory.equals("Add New Category", ignoreCase = true)) {
                showAddCategoryDialog { newCategory ->
                    categoryPreferences.saveCategory(newCategory)
                    addCategoryAdapter.clear()
                    addCategoryAdapter.addAll(categoryPreferences.getCategories())
                    addTaskCategoryDropdown.setText(newCategory, false)
                }
            }
        }

        // Set up priority Spinner for Add Task
        val priorityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        addTaskPrioritySpinner.adapter = priorityAdapter
        val timePickerBtn = addTaskDialog.findViewById<Button>(R.id.timePickerBtn)
        val tvSelectedTime = addTaskDialog.findViewById<TextView>(R.id.tvSelectedTime)

        timePickerBtn.setOnClickListener {
            showTimePicker(this@MainActivity) { selectedTime ->
                selectedDueTime = selectedTime
                val formattedTime =
                    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(selectedTime))
                tvSelectedTime.text = "Time: $formattedTime"
                tvSelectedTime.visibility = View.VISIBLE
            }
        }

        saveTaskBtn.setOnClickListener {
            if (validateEditText(addETTitle, addETTitleL)
                && validateEditText(addETDesc, addETDescL)
            ) {
                val selectedCategoryAdd = addTaskCategoryDropdown.text.toString()
                val selectedPriority =
                    when (addTaskPrioritySpinner.selectedItem.toString().trim()) {
                        "High" -> 3
                        "Normal" -> 2
                        "Low" -> 1

                        else -> {
                            0
                        }
                    }

// Call from the activity when saving a new task
                val title = addETTitle.text.toString()
                val description = addETDesc.text.toString()
                val reminderTime =
                    selectedDueTime // This should be the reminder time set by the user

// Insert task
                CoroutineScope(Dispatchers.IO).launch {
                    taskViewModel.insertOrUpdateTask(
                        title,
                        description,
                        selectedPriority,
                        Date(),
                        selectedCategoryAdd,
                        reminderTime
                    )
                }

                hideKeyBoard(it)
                addTaskDialog.dismiss()
//                taskViewModel.insertTask(newTask)
            }
        }
        // Add task end


        // Update Task Start
        val updateETTitle = updateTaskDialog.findViewById<TextInputEditText>(R.id.edTaskTitle)
        val updateETTitleL = updateTaskDialog.findViewById<TextInputLayout>(R.id.edTaskTitleL)
        val timePickerBtnET = updateTaskDialog.findViewById<Button>(R.id.edtimePickerBtn)
        val tvSelectedTimeET = updateTaskDialog.findViewById<TextView>(R.id.edtvSelectedTime)
        val priorityAdapterET = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)
        priorityAdapterET.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        updateETTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(updateETTitle, updateETTitleL)
            }

        })

        val updateETDesc = updateTaskDialog.findViewById<TextInputEditText>(R.id.edTaskDesc)
        val updateETDescL = updateTaskDialog.findViewById<TextInputLayout>(R.id.edTaskDescL)

        updateETDesc.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(updateETDesc, updateETDescL)
            }
        })

        val updateCloseImg = updateTaskDialog.findViewById<ImageView>(R.id.closeImg)
        updateCloseImg.setOnClickListener { updateTaskDialog.dismiss() }

        val updateTaskBtn = updateTaskDialog.findViewById<Button>(R.id.updateTaskBtn)
        // For Update Task Dialog

        val updateTaskCategoryDropdown =
            updateTaskDialog.findViewById<AutoCompleteTextView>(R.id.taskCategoryDropdown)
        val updateCategoryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            categoryPreferences.getCategories()
        )
        updateTaskCategoryDropdown.setAdapter(updateCategoryAdapter)

        updateTaskCategoryDropdown.setOnItemClickListener { _, _, position, _ ->
            val selectedCategory = updateTaskCategoryDropdown.text.toString()
            if (selectedCategory.equals("Add New Category", ignoreCase = true)) {
                showAddCategoryDialog { newCategory ->
                    categoryPreferences.saveCategory(newCategory)
                    updateCategoryAdapter.clear()
                    updateCategoryAdapter.addAll(categoryPreferences.getCategories())
                    updateTaskCategoryDropdown.setText(newCategory, false)
                }
            }
        }

        val selectedCategory = addTaskCategoryDropdown.text.toString()
        // For Update Dialog, dynamically set the selected priority:
        updateTaskPrioritySpinner.adapter = priorityAdapterET
        isListMutableLiveData.observe(this) {
            if (it) {
                mainBinding.taskRV.layoutManager = LinearLayoutManager(
                    this, LinearLayoutManager.VERTICAL, false
                )
                mainBinding.listOrGridImg.setImageResource(R.drawable.ic_view_module)
            } else {
                mainBinding.taskRV.layoutManager = StaggeredGridLayoutManager(
                    2, LinearLayoutManager.VERTICAL
                )
                mainBinding.listOrGridImg.setImageResource(R.drawable.ic_view_list)
            }
        }

        mainBinding.listOrGridImg.setOnClickListener {
            isListMutableLiveData.postValue(!isListMutableLiveData.value!!)
        }

        val taskRVVBListAdapter =
            TaskRVVBListAdapter(isListMutableLiveData) { type, position, task ->
                if (type == "delete") {
                    taskViewModel
                        // Deleted Task
//                .deleteTask(task)
                        .deleteTaskUsingId(task.id)

                    // Restore Deleted task
                    restoreDeletedTask(task)
                } else if (type == "update") {
                    updateETTitle.setText(task.title)
                    updateETDesc.setText(task.description)
                    timePickerBtnET.setOnClickListener {
                        showTimePicker(this@MainActivity) { selectedTime ->
                            selectedDueTime = selectedTime
                            val formattedTime =
                                SimpleDateFormat("hh:mm a", Locale.getDefault()).format(
                                    Date(
                                        selectedTime
                                    )
                                )
                            tvSelectedTimeET.text = "Time: $formattedTime"
                            tvSelectedTimeET.visibility = View.VISIBLE
                        }
                    }
                    updateTaskBtn.setOnClickListener {
                        if (validateEditText(updateETTitle, updateETTitleL)
                            && validateEditText(updateETDesc, updateETDescL)
                        ) {
                            val selectedPriority =
                                when (updateTaskPrioritySpinner.selectedItem.toString().trim()) {
                                    "High" -> 3
                                    "Normal" -> 2
                                    "Low" -> 1
                                    else -> {
                                        0
                                    }
                                }


                            // Call from the activity when updating an existing task
                            val taskId = task.id // Get the task ID you are updating
                            val title = updateETTitle.text.toString().trim()
                            val description = updateETDesc.text.toString().trim()
                            val priority = selectedPriority
                            val category = selectedCategory
                            val reminderTime = selectedDueTime

// Update task
                            CoroutineScope(Dispatchers.IO).launch {
                                taskViewModel.updateTask(
                                    taskId,
                                    title,
                                    description,
                                    priority,
                                    category,
                                    reminderTime
                                )
                            }

                            hideKeyBoard(it)
                            updateTaskDialog.dismiss()
                        }
                    }
                    updateTaskDialog.show()
                }
            }
        mainBinding.taskRV.adapter = taskRVVBListAdapter
        ViewCompat.setNestedScrollingEnabled(mainBinding.taskRV, false)
        taskRVVBListAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
//                mainBinding.taskRV.smoothScrollToPosition(positionStart)
                mainBinding.nestedScrollView.smoothScrollTo(0, positionStart)
            }
        })
        callGetTaskList(taskRVVBListAdapter)
        callSortByLiveData()
        statusCallback()
        callSearch()
        checkalarmPermission(this@MainActivity)

    }

    private fun checkalarmPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager: AlarmManager =
                context.getSystemService<AlarmManager>()!! as AlarmManager
            when {
                // If permission is granted, proceed with scheduling exact alarms.
                alarmManager.canScheduleExactAlarms() -> {
//                    longToastShow("can schedule")
                }

                else -> {
                    // Ask users to go to exact alarm page in system settings.
                    startActivity(Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                }
            }
        }
    }

    private fun isNotifPermEnabled(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                TODO("VERSION.SDK_INT < M")
            }
        ) {
            return false
        } else {
            return false
        }
    }

    private fun restoreDeletedTask(deletedTask: Task) {
        val snackBar = Snackbar.make(
            mainBinding.root, "Deleted '${deletedTask.title}'",
            Snackbar.LENGTH_LONG
        )
        snackBar.setAction("Undo") {
            taskViewModel.insertTask(deletedTask)
        }
        snackBar.show()
    }

    private fun callSearch() {
        mainBinding.edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(query: Editable) {
                if (query.toString().isNotEmpty()) {
                    taskViewModel.searchTaskList(query.toString())
                } else {
                    callSortByLiveData()
                }
            }
        })

        mainBinding.edSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyBoard(v)
                return@setOnEditorActionListener true
            }
            false
        }

        callSortByDialog()
    }

    private fun callSortByLiveData() {
        taskViewModel.sortByLiveData.observe(this) {
            taskViewModel.getTaskList(it.second, it.first)
        }
    }

    private fun callSortByDialog() {
        var checkedItem = 0   // 2 is default item set
        val items =
            arrayOf(
                "Category Ascending",
                "Category Descending",
                "Date Ascending",
                "Date Descending",
                "Priority Ascending",
                "Priority Descending"
            )

        mainBinding.sortImg.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Sort By")
                .setPositiveButton("Ok") { _, _ ->
                    when (checkedItem) {
                        0 -> {
                            taskViewModel.setSortBy(Pair("categoryId", true))
                        }

                        1 -> {
                            taskViewModel.setSortBy(Pair("categoryId", false))
                        }

                        2 -> {
                            taskViewModel.setSortBy(Pair("date", true))
                        }

                        3 -> {
                            taskViewModel.setSortBy(Pair("date", false))
                        }

                        4 -> {
                            taskViewModel.setSortBy(Pair("priority", true))

                        }

                        else -> {
                            taskViewModel.setSortBy(Pair("priority", false))

                        }
                    }
                }
                .setSingleChoiceItems(items, checkedItem) { _, selectedItemIndex ->
                    checkedItem = selectedItemIndex
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun statusCallback() {
        taskViewModel
            .statusLiveData
            .observe(this) {
                when (it.status) {
                    Status.LOADING -> {
                        loadingDialog.show()
                    }

                    Status.SUCCESS -> {
                        loadingDialog.dismiss()
                        when (it.data as StatusResult) {
                            Added -> {
                                Log.d("StatusResult", "Added")
                            }

                            Deleted -> {
                                Log.d("StatusResult", "Deleted")

                            }

                            Updated -> {
                                Log.d("StatusResult", "Updated")

                            }
                        }
                        it.message?.let { it1 -> longToastShow(it1) }
                    }

                    Status.ERROR -> {
                        loadingDialog.dismiss()
                        it.message?.let { it1 -> longToastShow(it1) }
                    }
                }
            }
    }

    private fun callGetTaskList(taskRecyclerViewAdapter: TaskRVVBListAdapter) {

        CoroutineScope(Dispatchers.Main).launch {
            taskViewModel
                .taskStateFlow
                .collectLatest {
                    Log.d("status", it.status.toString())

                    when (it.status) {
                        Status.LOADING -> {
                            loadingDialog.show()
                        }

                        Status.SUCCESS -> {
                            loadingDialog.dismiss()
                            it.data?.collect { taskList ->
                                taskRecyclerViewAdapter.submitList(taskList)
                            }
                        }

                        Status.ERROR -> {
                            loadingDialog.dismiss()
                            it.message?.let { it1 -> longToastShow(it1) }
                        }
                    }

                }
        }
    }

    private fun showAddCategoryDialog(onCategoryAdded: (String) -> Unit) {
        val dialog = Dialog(this, R.style.DialogCustomTheme).apply {
            setContentView(R.layout.add_category_dialog)
            val categoryNameEditText =
                findViewById<TextInputEditText>(R.id.categoryNameEditText)
            val addCategoryButton = findViewById<Button>(R.id.addCategoryButton)
            val cancelButton = findViewById<Button>(R.id.cancelButton)

            addCategoryButton.setOnClickListener {
                val newCategory = categoryNameEditText.text.toString().trim()
                if (newCategory.isNotEmpty()) {
                    onCategoryAdded(newCategory)
                    dismiss()
                } else {
                    categoryNameEditText.error = "Category name cannot be empty"
                }
            }
            cancelButton.setOnClickListener { dismiss() }
        }
        dialog.show()
    }

}