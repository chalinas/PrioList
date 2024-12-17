package com.chalinas.priolist.models


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date




@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Reminder::class,
            parentColumns = ["reminderId"],
            childColumns = ["reminderId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "taskId")
    val id: String,

    @ColumnInfo(name = "taskTitle")
    val title: String,

    val description: String,

    val date: Date,

    @ColumnInfo(name = "categoryId", index = true)
    val categoryId: Int?, // Foreign key

    @ColumnInfo(name = "reminderId", index = true)
    val reminderId: Int?, // Foreign key

    val priority: Int, // Task priority for sorting

    @ColumnInfo(name = "dueTime")
    val dueTime: Long // Timestamp for reminders
)


