package dev.chalinas.priolist.data;

import java.util.List;
import dev.chalinas.priolist.data.CategoryDao
import dev.chalinas.priolist.data.Category
import kotlinx.coroutines.flow.Flow

public class CategoryRepository(private val categoryDao: CategoryDao) {
    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()

    suspend fun insert(category: Category) {
        categoryDao.insertCategory(category)
    }
}
