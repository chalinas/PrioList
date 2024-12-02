package com.chalinas.priolist

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.chalinas.priolist.adapters.TaskRecyclerViewAdapter
import com.chalinas.priolist.databinding.ActivityMainBinding
import com.chalinas.priolist.models.Task
import com.chalinas.priolist.utils.Status
import com.chalinas.priolist.utils.clearEditText
import com.chalinas.priolist.utils.longToastShow
import com.chalinas.priolist.utils.setupDialog
import com.chalinas.priolist.utils.validateEditText
import com.chalinas.priolist.viewmodels.TaskViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private val mainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val addTaskDialog: Dialog by lazy {
        Dialog(this,R.style.DialogCustomTheme).apply{
            setupDialog(R.layout.add_task_dialog)
        }
    }

    private val updateTaskDialog: Dialog by lazy {
        Dialog(this,R.style.DialogCustomTheme).apply{
            setupDialog(R.layout.update_task_dialog)
        }
    }

    private val loadingDialog: Dialog by lazy {
        Dialog(this,R.style.DialogCustomTheme).apply{
            setupDialog(R.layout.loading_dialog)
        }
    }

    private val taskViewModel : TaskViewModel by lazy {
        ViewModelProvider(this)[TaskViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)


        // Empieza incluir tarea
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
        saveTaskBtn.setOnClickListener {
            if(validateEditText(addETTitle, addETTitleL)
                && validateEditText(addETDesc, addETDescL)
                ){
                addTaskDialog.dismiss()
                val newTask = Task(
                    UUID.randomUUID().toString(),
                    addETTitle.text.toString().trim(),
                    addETDesc.text.toString().trim(),
                    Date()
                )
                taskViewModel.insertTask(newTask).observe(this){
                    when(it.status){
                        Status.LOADING -> loadingDialog.show()
                        Status.SUCCESS -> {
                            loadingDialog.dismiss()
                            if (it.data?.toInt() != -1){
                                longToastShow("Task saved!")
                            }
                        }
                        Status.ERROR -> {
                            loadingDialog.dismiss()
                            it.message?.let { it1 ->longToastShow(it1) }
                        }
                    }
                }

            }
        }
        //Acaba incluir tarea

        //Empieza actualizar tarea
        val updateETTitle = updateTaskDialog.findViewById<TextInputEditText>(R.id.edTaskTitle)
        val updateETTitleL = updateTaskDialog.findViewById<TextInputLayout>(R.id.edTaskTitleL)
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

        //Acaba actualizar tarea

        val taskRecyclerViewAdapter = TaskRecyclerViewAdapter{type,position, task ->
            if(type == "delete"){
            taskViewModel
                .deleteTask(task)
                .observe(this){
                    when(it.status){
                        Status.LOADING -> loadingDialog.show()
                        Status.SUCCESS -> {
                            loadingDialog.dismiss()
                            if (it.data?.toInt() != -1){
                                longToastShow("Task deleted!")
                            }
                        }
                        Status.ERROR -> {
                            loadingDialog.dismiss()
                            it.message?.let { it1 ->longToastShow(it1) }
                        }
                    }
                }
             }
        else if (type == "update"){
            updateETTitle.setText(task.title)
            updateETDesc.setText(task.description)
            updateTaskBtn.setOnClickListener {
                if (validateEditText(updateETTitle, updateETTitleL)
                    && validateEditText(updateETDesc, updateETDescL)
                ) {
                    val updateTask = Task(
                        task.id,
                        updateETTitle.text.toString().trim(),
                        updateETDesc.text.toString().trim(),
                        Date()
                    )
                    updateTaskDialog.dismiss()
                    loadingDialog.show()
                    taskViewModel
                        //.updateTask(updateTask)
                        // SI SE CAMBIA FECHA ACTIVAR ARRIBA
                        .updateTaskParticularField(task.id,
                            updateETTitle.text.toString().trim(),
                            updateETDesc.text.toString().trim()
                        )
                        //SI NO SE CAMBIA FECHA AL FINAL DEJAR COMO ESTA
                        .observe(this) {
                            when (it.status) {
                                Status.LOADING -> loadingDialog.show()
                                Status.SUCCESS -> {
                                    loadingDialog.dismiss()
                                    if (it.data?.toInt() != -1) {
                                        longToastShow("Task updated!")
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
                updateTaskDialog.show()
            }
        }
        mainBinding.taskRV.adapter = taskRecyclerViewAdapter
        callGetTaskList(taskRecyclerViewAdapter)
    }




    private fun callGetTaskList(taskRecyclerViewAdapter: TaskRecyclerViewAdapter) {
        loadingDialog.show()
        CoroutineScope(Dispatchers.Main).launch {
        taskViewModel.getTaskList().collect{
            when(it.status){
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> {
                    it.data?.collect{taskList->
                    loadingDialog.dismiss()
                        taskRecyclerViewAdapter.addAllTask(taskList)
                    }
                }
                Status.ERROR -> {
                    loadingDialog.dismiss()
                    it.message?.let { it1 ->longToastShow(it1) }
                }
            }
        }
        }

    }
}
