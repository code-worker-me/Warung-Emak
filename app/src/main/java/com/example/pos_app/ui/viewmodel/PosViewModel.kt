package com.example.pos_app.ui.viewmodel

import androidx.lifecycle.ViewModel
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PosViewModel(
    private val repository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            if (transactionRepository.allTransactions.first().isEmpty()) {
                val sampleTitles = listOf(
                    "Penjualan Produk POS",
                    "Transfer to Adebayo (UBA)",
                    "Exchange to GHS",
                    "Card Top-up",
                    "Deposit via Debit Card (Visa)",
                    "Card Payment (Netflix)",
                    "Pembayaran Suplier",
                    "Restock Barang",
                    "Penjualan Grosir",
                    "Withdrawal ATM"
                )
                val times = listOf("3:12 PM", "2:12 PM", "1:30 PM", "11:15 AM", "10:00 AM", "8:30 AM")

                for (i in 1..100) {
                    val title = sampleTitles.random()
                    val dateGroup = when {
                        i <= 15 -> "Today"
                        i <= 35 -> "Yesterday"
                        i <= 65 -> "2 days ago"
                        else -> "Last Week"
                    }
                    val timestamp = times.random()
                    val amount = Math.random() * 950000 + 50000
                    val isIncome = i % 2 == 0 || i % 3 == 0

                    transactionRepository.insert(
                        TransactionEntity(
                            title = "$title #$i",
                            timestamp = timestamp,
                            dateGroup = dateGroup,
                            amount = amount,
                            isIncome = isIncome
                        )
                    )
                }
            }
        }
    }

    val products: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val categories: StateFlow<List<CategoryEntity>> = categoryRepository.allCategories
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val transactions: StateFlow<List<TransactionEntity>> = transactionRepository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insertCategory(name: String) {
        viewModelScope.launch {
            categoryRepository.insertCategory(CategoryEntity(name = name))
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(category)
        }
    }

    val totalProfit: StateFlow<Double> = products.map { list ->
        list.sumOf { (it.price - it.buyPrice) * it.quantity }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    enum class SortOrder { NONE, HIGHEST_STOCK, LOWEST_STOCK }
    private val _sortOrder = MutableStateFlow(SortOrder.NONE)
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    val filteredProducts: StateFlow<List<ProductEntity>> = combine(products, _searchQuery, _sortOrder) { list, query, sort ->
        val filtered = if (query.isBlank()) {
            list
        } else {
            list.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true)
            }
        }

        when (sort) {
            SortOrder.HIGHEST_STOCK -> filtered.sortedByDescending { it.quantity }
            SortOrder.LOWEST_STOCK -> filtered.sortedBy { it.quantity }
            SortOrder.NONE -> filtered
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addProduct(name: String, buyPrice: Double, price: Double, quantity: Int, category: String, imageUrl: String?) {
        if (name.isBlank() || price <= 0 || quantity < 0) return

        viewModelScope.launch {
            val product = ProductEntity(
                name = name,
                buyPrice = buyPrice,
                price = price,
                quantity = quantity,
                category = category,
                imageUrl = imageUrl
            )
            repository.insert(product)
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
