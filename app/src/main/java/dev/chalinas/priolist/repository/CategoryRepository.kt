package dev.chalinas.priolist.repository

import dev.chalinas.priolist.data.Category


interface CategoryRepository {
    fun getAllCategories(): List<Category>
    fun getCategoryById(id: Int): Category?
    fun createCategory(category: Category): Int
    fun updateCategory(category: Category): Int
    fun deleteCategory(id: Int): Int
}