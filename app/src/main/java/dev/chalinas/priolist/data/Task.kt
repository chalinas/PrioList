package dev.chalinas.priolist.data

import java.util.Date


data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val dueDate: Date,
    val priority: Priority,
    val isCompleted: Boolean
)




