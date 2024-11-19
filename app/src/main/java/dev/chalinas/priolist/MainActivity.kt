package dev.chalinas.priolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.chalinas.priolist.ui.theme.PrioListTheme

class MainActivity : AppCompatActivity() {
    private lateinit var taskService: TaskService
    private lateinit var categoryService: CategoryService
    private lateinit var reminderService: ReminderService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskService = TaskService(TaskRepositoryImpl(AppDatabase.getInstance(this)))
        categoryService = CategoryService(CategoryRepositoryImpl(AppDatabase.getInstance(this)))
        reminderService = ReminderService(ReminderRepositoryImpl(AppDatabase.getInstance(this)))

        // Utilizar los servicios para interactuar con la base de datos
        val tasks = taskService.getAllTasks()
        val categories = categoryService.getAllCategories()
        val reminders = reminderService.getAllReminders()
    }
}