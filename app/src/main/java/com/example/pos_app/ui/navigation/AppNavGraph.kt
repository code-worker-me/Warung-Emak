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
import com.example.pos_app.ui.viewmodel.PosViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Cashier : Screen("cashier")
    object Stock : Screen("stock")
}

@Composable
fun AppNavGraph(viewModel: PosViewModel = viewModel(), modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val products by viewModel.products.collectAsState()
    val totalProfit by viewModel.totalProfit.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredProducts by viewModel.filteredProducts.collectAsState()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                totalProfit = totalProfit,
                onNavigateToCashier = { navController.navigate(Screen.Cashier.route) },
                onNavigateToStock = { navController.navigate(Screen.Stock.route) }
            )
        }
        composable(Screen.Cashier.route) {
            CashierScreen(productList = products)
        }
        composable(Screen.Stock.route) {
            StockScreen(
                productList = filteredProducts,
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
                }
            )
        }
        composable("add_product") {
            AddProductScreen(
                onBackClicked = { navController.popBackStack() },
            ) { name, price, buyPrice, quantity, category, imageUri ->
                // Panggil logika dari ViewModel
                viewModel.addProduct(name, price, buyPrice, quantity, category, imageUri)
                navController.popBackStack() // Kembali ke halaman stock setelah disimpan
            }
        }
        composable("edit_product") {
            val productToEdit = viewModel.selectedProduct.collectAsState().value
            if (productToEdit != null) {
                EditProductScreen(
                    product = productToEdit,
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