package com.example.pos_app.ui.stock

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pos_app.R
import com.example.pos_app.data.local.ProductEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockScreen(
    productList: List<ProductEntity>,
    onAddClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onEditClicked: (ProductEntity) -> Unit,
    onDeleteBatch: (List<Int>) -> Unit
) {
    var selectedProductIds by remember { mutableStateOf(setOf<Int>()) }
    val isSelectionMode = selectedProductIds.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isSelectionMode) "${selectedProductIds.size} dipilih" else stringResource(
                        R.string.atur_stock
                    ))
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isSelectionMode) {
                            selectedProductIds = emptySet() // Reset seleksi saat back jika mode aktif
                        } else {
                            onBackClicked()
                        }
                    }) {
                        Icon(
                            imageVector = if (isSelectionMode) Icons.Default.Close else Icons.Default.ArrowBack,
                            contentDescription = "Kembali / Batal"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isSelectionMode) {
                    FloatingActionButton(
                        onClick = {
                            onDeleteBatch(selectedProductIds.toList())
                            selectedProductIds = emptySet()
                        },
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus Terpilih")
                    }
                }

                if (productList.isNotEmpty()) {
                    FloatingActionButton(onClick = onAddClicked) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah Produk")
                    }
                }
            }
        }
    ) { padding ->
        if (productList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Belum ada produk",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(onClick = onAddClicked) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tambah Produk")
                    }
                }
            }
        } else {
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(productList) { product ->
                val isSelected = selectedProductIds.contains(product.id)

                // panggil fungsi SelectableProductCard
                SelectableProductCard(
                    product = product,
                    isSelected = isSelected,
                    onLongClick = {
                        // Long click untuk mulai memilih atau menambah pilihan
                        selectedProductIds = if (isSelected) {
                            selectedProductIds - product.id
                        } else {
                            selectedProductIds + product.id
                        }
                    },
                    onClick = {
                        if (isSelectionMode) {
                            // Jika sudah dalam mode seleksi, klik biasa berfungsi untuk toggle pilih/batal pilih
                            selectedProductIds = if (isSelected) {
                                selectedProductIds - product.id
                            } else {
                                selectedProductIds + product.id
                            }
                        } else {
                            // Jika tidak dalam mode seleksi, jalankan fungsi edit
                            onEditClicked(product)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }
        }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectableProductCard(
    product: ProductEntity,
    isSelected: Boolean,
    onLongClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            // Ubah warna background card jika sedang dipilih
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (product.imageUrl.isNullOrBlank()) {
                Image(
                    painter = painterResource(R.drawable.product_default),
                    contentDescription = "Product Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            } else {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.product_default),
                    error = painterResource(id = R.drawable.product_default),
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            // Kolom Nama & Stok di tengah (mengisi sisa ruang menggunakan weight(1f))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Stok: ${product.quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Harga di posisi paling kanan
            Text(
                text = "Rp ${String.format("%,.0f", product.price)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
