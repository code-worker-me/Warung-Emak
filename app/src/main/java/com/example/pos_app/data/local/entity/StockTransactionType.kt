package com.example.pos_app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class StockTransactionType { IN, OUT, ADJUSTMENT}

@Entity(
    tableName = "stock_histories",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["product_id"])]
)

data class StockHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "product_id") val productId: Long,
    val type: StockTransactionType,
    val quantity: Int,
    @ColumnInfo(name = "initial_stock") val initialStock: Int,
    @ColumnInfo(name = "final_stock") val finalStock: Int,
    val notes: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)