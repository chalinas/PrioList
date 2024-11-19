package dev.chalinas.priolist.service

import dev.chalinas.priolist.data.Category
import dev.chalinas.priolist.repository.CategoryRepository


class CategoryService(private val categoryRepository: CategoryRepository) {
    fun getAllCategories(): List<Category> {
        return categoryRepository.getAllCategories()
    }

    fun getCategoryById(id: Int): Category? {
        return categoryRepository.getCategoryById(id)
    }

    fun createCategory(category: Category): Int {
        return categoryRepository.createCategory(category)
    }

    fun updateCategory(category: Category): Int {
        return categoryRepository.updateCategory(category)
    }

    fun deleteCategory(id: Int): Int {
        return categoryRepository.deleteCategory(id)
    }
}