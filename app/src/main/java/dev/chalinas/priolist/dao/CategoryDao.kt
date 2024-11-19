package dev.chalinas.priolist.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.chalinas.priolist.data.Category

@Dao
interface CategoryDao {

    @Insert
    fun insertCategory(category: Category): Long

    @Query("SELECT * FROM categories")
    fun getAllCategories(): List<Category>

    @Query("SELECT * FROM categories WHERE id = :id")
    fun getCategoryById(id: Int): Category?

    @Update
    fun updateCategory(category: Category): Int

    @Delete
    fun deleteCategory(category: Category): Int
}