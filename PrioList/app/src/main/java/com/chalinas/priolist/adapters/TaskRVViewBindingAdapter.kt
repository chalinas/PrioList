package com.chalinas.priolist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chalinas.priolist.models.Task
import com.chalinas.priolist.R
import com.chalinas.priolist.databinding.ViewTaskLayoutBinding
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Locale

class TaskRVViewBindingAdapter(
    private val deleteUpdateCallback: (type: String, position: Int, task: Task) -> Unit
):
RecyclerView.Adapter<TaskRVViewBindingAdapter.ViewHolder>() {

    private val taskList = arrayListOf<Task>()

    class ViewHolder(val viewTaskLayoutBinding: ViewTaskLayoutBinding) : RecyclerView.ViewHolder(viewTaskLayoutBinding.root)

    fun addAllTask(newTaskList: List<Task>){
        taskList.clear()
        taskList.addAll(newTaskList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ViewTaskLayoutBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val task = taskList[position]
        holder.viewTaskLayoutBinding.titleTxt.text = task.title
        holder.viewTaskLayoutBinding.descTxt.text = task.description

        val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss ",Locale.getDefault())
        holder.viewTaskLayoutBinding.dateTxt.text = dateFormatter.format(task.date)

        holder.viewTaskLayoutBinding.deleteImg.setOnClickListener {
            if (holder.adapterPosition != -1) {
                deleteUpdateCallback("delete", holder.adapterPosition, task)
            }
        }
        holder.viewTaskLayoutBinding.editImg.setOnClickListener {
            if (holder.adapterPosition != -1) {
                deleteUpdateCallback("update", holder.adapterPosition, task)
            }
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

}