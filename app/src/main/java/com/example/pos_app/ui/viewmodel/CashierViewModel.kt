package com.example.pos_app.ui.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos_app.data.local.entity.CategoryEntity
import com.example.pos_app.data.local.entity.ProductEntity
import com.example.pos_app.data.local.entity.TransactionEntity
import com.example.pos_app.data.repository.CategoryRepository
import com.example.pos_app.data.repository.ProductRepository
import com.example.pos_app.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CashierViewModel(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    application: Application
) : AndroidViewModel(application) {

    val products: StateFlow<List<ProductEntity>> = productRepository.allProducts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val categories: StateFlow<List<CategoryEntity>> = categoryRepository.allCategories.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    val filteredProducts: StateFlow<List<ProductEntity>> = combine(products, _searchQuery, _selectedCategory) { list, query, selectedCat ->
        val availableList = list.filter { it.quantity > 0 }
        val queryFiltered = if (query.isBlank()) availableList else availableList.filter { it.name.contains(query, ignoreCase = true) }
        if (selectedCat == "Semua") queryFiltered else queryFiltered.filter { it.category == selectedCat }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // State untuk Keranjang Belanja (Map<ProductEntity, Quantity>)
    private val _cartItems = MutableStateFlow<Map<ProductEntity, Int>>(emptyMap())
    val cartItems: StateFlow<Map<ProductEntity, Int>> = _cartItems.asStateFlow()

    fun addToCart(product: ProductEntity) {
        val currentMap = _cartItems.value.toMutableMap()
        val currentQty = currentMap[product] ?: 0
        if (currentQty < product.quantity) {
            currentMap[product] = currentQty + 1
            _cartItems.value = currentMap
        } else {
            Toast.makeText(getApplication(), "Stok tidak mencukupi", Toast.LENGTH_SHORT).show()
        }
    }

    fun removeFromCart(product: ProductEntity) {
        val currentMap = _cartItems.value.toMutableMap()
        val currentQty = currentMap[product] ?: 0
        if (currentQty > 1) {
            currentMap[product] = currentQty - 1
        } else {
            currentMap.remove(product)
        }
        _cartItems.value = currentMap
    }

    fun clearCart() {
        _cartItems.value = emptyMap()
    }

    fun checkout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val currentCart = _cartItems.value
            val totalPrice = currentCart.entries.sumOf { (product, qty) -> product.price * qty }
            val totalItems = currentCart.values.sum()

            for ((product, qtyPurchased) in currentCart) {
                val newQty = (product.quantity - qtyPurchased).coerceAtLeast(0)
                val updatedProduct = product.copy(quantity = newQty)
                productRepository.update(updatedProduct)
            }

            val timeString = SimpleDateFormat("h:mm a", Locale.forLanguageTag("id-ID")).format(Date())
            val transaction = TransactionEntity(
                title = "Penjualan POS ($totalItems item)",
                timestamp = timeString,
                dateGroup = "Today",
                amount = totalPrice,
                isIncome = true
            )
            transactionRepository.insert(transaction)

            _cartItems.value = emptyMap()
            onSuccess()
        }
    }
}
