package com.donfuy.android.today.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.donfuy.android.today.model.TaskItem

@Database(entities = [TaskItem::class], version = 6, exportSchema = false)
abstract class TaskItemDatabase : RoomDatabase() {
    abstract fun taskItemDao(): TaskItemDao

    companion object {
        @Volatile
        private var INSTANCE: TaskItemDatabase? = null

        fun getDatabase(context: Context): TaskItemDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskItemDatabase::class.java,
                    "item_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                return instance
            }
        }
    }
}