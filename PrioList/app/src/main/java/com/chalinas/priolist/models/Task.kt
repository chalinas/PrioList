package com.chalinas.priolist.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Task(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "taksId")
    val id: String,
    @ColumnInfo
    val title: String,
    val description: String,
    val date: Date,
)
