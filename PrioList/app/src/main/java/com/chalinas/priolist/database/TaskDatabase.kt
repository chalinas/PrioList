package com.chalinas.priolist.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.chalinas.priolist.converters.TypeConverter
import com.chalinas.priolist.models.Task

@Database(
    entities = [Task::class],
    version = 1
)
@TypeConverters(TypeConverter::class)
abstract class TaskDatabase : RoomDatabase() {
    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null
        fun getInstance(context: Context): TaskDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}