package com.example.pos_app

import android.app.Application
import com.example.pos_app.data.local.AppDatabase
import com.example.pos_app.data.repository.CategoryRepository
import com.example.pos_app.data.repository.ProductRepository
import com.example.pos_app.data.repository.TransactionRepository

interface AppContainer {
    val database: AppDatabase
    val productRepository: ProductRepository
    val categoryRepository: CategoryRepository
    val transactionRepository: TransactionRepository
}

class DefaultAppContainer(private val applicationContext: android.content.Context) : AppContainer {
    override val database: AppDatabase by lazy {
        AppDatabase.getDatabase(applicationContext)
    }

    override val productRepository: ProductRepository by lazy {
        ProductRepository(database.productDao())
    }

    override val categoryRepository: CategoryRepository by lazy {
        CategoryRepository(database.categoryDao())
    }

    override val transactionRepository: TransactionRepository by lazy {
        TransactionRepository(database.transactionDao())
    }
}

class PosApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
