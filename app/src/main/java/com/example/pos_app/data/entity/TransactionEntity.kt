package com.example.pos_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val timestamp: String,
    val dateGroup: String, // e.g. "Today", "Yesterday"
    val amount: Double,
    val isIncome: Boolean
)
