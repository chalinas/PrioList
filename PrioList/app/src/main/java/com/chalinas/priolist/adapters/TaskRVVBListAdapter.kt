package com.chalinas.priolist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chalinas.priolist.databinding.ViewTaskLayoutBinding
import com.chalinas.priolist.models.Task
import java.text.SimpleDateFormat
import java.util.Locale

class TaskRVVBListAdapter(
    private val deleteUpdateCallback: (type: String, position: Int, task: Task) -> Unit
):
    ListAdapter<Task,TaskRVVBListAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(val viewTaskLayoutBinding: ViewTaskLayoutBinding) : RecyclerView.ViewHolder(viewTaskLayoutBinding.root)

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
        val task = getItem(position)
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

    class DiffCallback : DiffUtil.ItemCallback<Task>(){
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }

    }

}