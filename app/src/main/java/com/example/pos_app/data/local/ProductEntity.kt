package com.example.pos_app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val imageUrl: String?,
    val price: Double,
    val buyPrice: Double,
    val quantity: Int,
    val category: String,
    val expiredDate: String?
)
