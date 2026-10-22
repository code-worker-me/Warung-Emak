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
                productList = products,
                onBackClicked = { navController.popBackStack() },
                onAddClicked = {
                    // Contoh quick insert data dummy untuk testing CRUD
                    viewModel.addProduct(
                        name = "Produk Baru",
                        imageUrl = null,
                        price = 10000.0,
                        quantity = 50,
                        category = "Umum",
                        expiredDate = "2027-12-31"
                    )
                },
                onEditClicked = { product ->
                    viewModel.updateProduct(product.copy(quantity = product.quantity + 10))
                },
                onDeleteClicked = { product ->
                    viewModel.deleteProduct(product)
                }
            )
        }
    }
}