package com.example.pos_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos_app.data.local.AppDatabase
import com.example.pos_app.data.local.ProductEntity
import com.example.pos_app.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PosViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProductRepository

    init {
        val productDao = AppDatabase.getDatabase(application).productDao()
        repository = ProductRepository(productDao)
    }

    val products: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val totalProfit: StateFlow<Double> = products.map { list ->
        list.sumOf { (it.price * it.quantity) * 0.2 }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    fun addProduct(name: String, price: Double, quantity: Int, category: String, imageUrl: String?) {
        if (name.isBlank() || price <= 0 || quantity < 0) return // Validasi sederhana

        viewModelScope.launch {
            repository.insert(
                ProductEntity(
                    name = name,
                    price = price,
                    quantity = quantity,
                    category = category,
                    imageUrl = imageUrl,
                    expiredDate = null
                )
            )
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.update(product)
        }
    }

    private val _selectedProduct = MutableStateFlow<ProductEntity?>(null)
    val selectedProduct: StateFlow<ProductEntity?> = _selectedProduct.asStateFlow()

    fun selectProductForEdit(product: ProductEntity) {
        _selectedProduct.value = product
    }

    fun deleteProduct(ids: List<Int>) {
        viewModelScope.launch {
            ids.forEach { id ->
                val productToDelete = products.value.find { it.id == id }
                productToDelete?.let { repository.delete(it) }
            }
        }
    }
}