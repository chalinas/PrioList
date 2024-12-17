package com.chalinas.priolist.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Relation

data class TaskWithDetails(
    @Embedded val task: Task, // Embeds all Task fields
    @ColumnInfo(name = "categoryName") val categoryName: String?, // Mapped from Category table
    @ColumnInfo(name = "reminderTime") val reminderTime: Long? // Mapped from Reminder table
)
