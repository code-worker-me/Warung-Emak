package com.example.pos_app.ui.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.pos_app.PosApplication

object AppViewModelProvider {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            PosViewModel(
                repository = posApplication().container.productRepository,
                categoryRepository = posApplication().container.categoryRepository,
                transactionRepository = posApplication().container.transactionRepository
            )
        }
        initializer {
            CashierViewModel(
                productRepository = posApplication().container.productRepository,
                categoryRepository = posApplication().container.categoryRepository,
                transactionRepository = posApplication().container.transactionRepository,
                application = posApplication()
            )
        }
    }
}

fun CreationExtras.posApplication(): PosApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PosApplication)
