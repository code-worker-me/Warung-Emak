package com.example.pos_app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pos_app.data.local.dao.CategoryDao
import com.example.pos_app.data.local.dao.ProductDao
import com.example.pos_app.data.local.dao.TransactionDao
import com.example.pos_app.data.local.entity.CategoryEntity
import com.example.pos_app.data.local.entity.ProductEntity
import com.example.pos_app.data.local.entity.TransactionEntity

@Database(
    entities = [ProductEntity::class, CategoryEntity::class, TransactionEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pos_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
