package com.chalinas.priolist.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.chalinas.priolist.converters.TypeConverter
import com.chalinas.priolist.dao.CategoryDao
import com.chalinas.priolist.dao.ReminderDao
import com.chalinas.priolist.dao.TaskDao
import com.chalinas.priolist.models.Category
import com.chalinas.priolist.models.Reminder
import com.chalinas.priolist.models.Task

@Database(
    entities = [Task::class, Category::class, Reminder::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(TypeConverter::class)
abstract class TaskDatabase : RoomDatabase() {

    abstract val taskDao: TaskDao
    abstract val categoryDao: CategoryDao
    abstract val reminderDao: ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null
        fun getInstance(context: Context): TaskDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_db"
                )
                    .fallbackToDestructiveMigration()
                    .build().also {
                        INSTANCE = it
                    }
            }
        }
    }
}
