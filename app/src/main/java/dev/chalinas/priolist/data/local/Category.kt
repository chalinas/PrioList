package dev.chalinas.priolist.data.local

import androidx.room.*
import java.time.LocalDateTime

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val categoryId: Int = 0,
    val userId: Int,
    val name: String,
    val color: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)