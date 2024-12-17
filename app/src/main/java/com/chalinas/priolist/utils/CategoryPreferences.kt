package com.chalinas.priolist.utils

import android.content.Context

class CategoryPreferences(context: Context) {
    private val sharedPreferences =
        context.getSharedPreferences("task_categories", Context.MODE_PRIVATE)

    fun getCategories(): MutableList<String> {
        val defaultCategories = setOf("Work", "Personal", "Shopping", "Add New Category")
        return sharedPreferences.getStringSet("categories", defaultCategories)?.toMutableList()
            ?: defaultCategories.toMutableList()
    }

    fun saveCategory(category: String) {
        val categories = getCategories().toMutableSet()
        categories.add(category)
        sharedPreferences.edit().putStringSet("categories", categories).apply()
    }
}
