package com.example.pos_app.data.local

import androidx.room.TypeConverter
import com.example.pos_app.data.local.entity.StockTransactionType

class Converters {
    @TypeConverter
    fromStockTransactionType(type: StockTransactionType): String = type.name

    @TypeConverter
    toStockTransactionType(value: String): StockTransactionType = StockTransactionType.valueOf(value)
}