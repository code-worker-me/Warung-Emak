package com.example.pos_app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String? = null,
    val name: String? = "",
    @ColumnInfo(name = "cost_price") val costPrice: Double,
    @ColumnInfo(name = "selling_price") val sellingPrice: Double,
    val stock: Int = 0,
    @ColumnInfo(name = "min_stock") val minStock: Int = 0,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
