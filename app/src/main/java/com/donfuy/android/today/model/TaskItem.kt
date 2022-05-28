package com.donfuy.android.today.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_item")
data class TaskItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val task: String,
    val checked: Boolean = false,
    val deleted: Boolean = false,
    val tomorrow: Boolean = false,
)
