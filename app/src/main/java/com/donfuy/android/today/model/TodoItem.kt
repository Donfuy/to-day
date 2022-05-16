package com.donfuy.android.today.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "todo_item")
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val task: String,
    val checked: Boolean = false,
    val position: Int = 0,
    val editing: Boolean = false
)
