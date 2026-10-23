package com.example.pos_app.ui.cashier

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pos_app.data.entity.ProductEntity

@Composable
fun CashierScreen(
    productList: List<ProductEntity>
) {
    val categories = listOf("Semua") + productList.map { it.category }.distinct()
    var selectedCategory by remember { mutableStateOf("Semua") }

    Scaffold { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
        ) {
            Text(text = "Kasir", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(12.dp))

            // Kategori Filter
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val filteredList = if (selectedCategory == "Semua") productList
            else productList.filter { it.category == selectedCategory }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList) { product ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            AsyncImage(
                                model = product.imageUrl?.takeIf { it.isNotBlank() },
                                contentDescription = product.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp),
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(id = android.R.drawable.ic_menu_gallery), // Gambar bawaan Android saat loading
                                error = painterResource(id = android.R.drawable.ic_menu_report_image) // Gambar bawaan saat URL kosong/error
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = product.name, style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "Rp ${product.price}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Stok: ${product.quantity}",
                                style = MaterialTheme.typography.bodySmall
                            )

                            product.expiredDate?.let {
                                if (it.isNotBlank()) {
                                    Text(
                                        text = "Exp: $it",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Red
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = { /* Aksi tambah ke keranjang */ },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("+ Jual")
                            }
                        }
                    }
                }
            }
        }
    }
}