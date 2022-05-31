package com.donfuy.android.today.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "todo_item")
data class TaskItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val task: String,
    val checked: Boolean = false,
    val deleted: Boolean = false,
    val tomorrow: Boolean = false,
    val creationDate: Date,
    val lastModified: Date?,
    val deletionDate: Date?
)
