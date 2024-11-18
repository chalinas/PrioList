package dev.chalinas.priolist.data.local

import kotlinx.coroutines.flow.Flow

public class CategoryRepository(private val categoryDao: CategoryDao) {
    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()

    suspend fun insert(category: Category) {
        categoryDao.insertCategory(category)
    }
}