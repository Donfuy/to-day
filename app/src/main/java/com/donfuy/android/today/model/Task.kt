package com.donfuy.android.today.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "task")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val task: String,
    val checked: Boolean = false,
    val binned: Boolean = false,
    val tomorrow: Boolean = false,
    val createdAt: Date,
    val lastModifiedAt: Date?,
    val deleteBy: Date?
)
