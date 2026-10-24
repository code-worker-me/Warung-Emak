package com.example.pos_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pos_app.ui.cashier.CashierScreen
import com.example.pos_app.ui.home.HomeScreen
import com.example.pos_app.ui.stock.AddProductScreen
import com.example.pos_app.ui.stock.EditProductScreen
import com.example.pos_app.ui.stock.StockScreen
import com.example.pos_app.ui.transaction.TransactionHistoryScreen
import com.example.pos_app.ui.viewmodel.AppViewModelProvider
import com.example.pos_app.ui.viewmodel.PosViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Cashier : Screen("cashier")
    object Stock : Screen("stock")
    object TransactionHistory : Screen("transaction_history")
}

@Composable
fun AppNavGraph(
    viewModel: PosViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val categoryList by viewModel.categories.collectAsState()
    val totalProfit by viewModel.totalProfit.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                totalProfit = totalProfit,
                onNavigateToCashier = { navController.navigate(Screen.Cashier.route) },
                onNavigateToStock = { navController.navigate(Screen.Stock.route) },
                onNavigateToTransactionHistory = { navController.navigate(Screen.TransactionHistory.route) }
            )
        }
        composable(Screen.Cashier.route) {
            CashierScreen(
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(Screen.TransactionHistory.route) {
            TransactionHistoryScreen(
                transactions = transactions,
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(Screen.Stock.route) {
            StockScreen(
                productList = filteredProducts,
                categoryList = categoryList,
                searchQuery = searchQuery,
                onSearchQueryChanged = { viewModel.setSearchQuery(it) },
                onSortSelected = { order -> viewModel.setSortOrder(order) },
                onBackClicked = { navController.popBackStack() },
                onAddClicked = { navController.navigate("add_product") },
                onEditClicked = { product ->
                    viewModel.selectProductForEdit(product)
                    navController.navigate("edit_product")
                },
                onDeleteBatch = { idsList ->
                    viewModel.deleteProduct(idsList)
                },
                onAddCategory = { name -> viewModel.insertCategory(name) },
                onDeleteCategory = { category -> viewModel.deleteCategory(category) }
            )
        }
        composable("add_product") {
            val categoryList by viewModel.categories.collectAsState()
            AddProductScreen(
                categoryList = categoryList,
                onBackClicked = { navController.popBackStack() },
            ) { name, price, buyPrice, quantity, category, imageUri ->
                viewModel.addProduct(name, buyPrice, price, quantity, category, imageUri)
                navController.popBackStack()
            }
        }
        composable("edit_product") {
            val productToEdit = viewModel.selectedProduct.collectAsState().value
            val categoryList by viewModel.categories.collectAsState()
            if (productToEdit != null) {
                EditProductScreen(
                    product = productToEdit,
                    categoryList = categoryList,
                    onBackClicked = { navController.popBackStack() },
                    onUpdateProduct = { updatedProduct ->
                        viewModel.updateProduct(updatedProduct)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
