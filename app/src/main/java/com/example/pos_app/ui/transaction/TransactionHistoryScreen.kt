package com.example.pos_app.ui.transaction

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pos_app.data.local.entity.TransactionEntity
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    transactions: List<TransactionEntity>,
    onBackClicked: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getInstance(Locale.forLanguageTag("id-ID")) }
    val groupedTransactions = transactions.groupBy { it.dateGroup }

    Scaffold(
        containerColor = Color(0xFFFFF2B4),
        topBar = {
            TopAppBar(
                title = { Text("Transaction History", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF4DE)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                if (transactions.isEmpty()) {
                    // Posisi di tengah layar secara vertikal dan horizontal
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada riwayat transaksi",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        groupedTransactions.forEach { (dateGroup, items) ->
                            item {
                                Text(
                                    text = dateGroup,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 5.dp)
                                )
                            }
                            items(items) { transaction ->
                                TransactionItemRow(transaction = transaction, currencyFormat = currencyFormat)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItemRow(transaction: TransactionEntity, currencyFormat: NumberFormat) {
    val amountFormatted = currencyFormat.format(transaction.amount)
    val amountText = if (transaction.isIncome) "+Rp $amountFormatted" else "-Rp $amountFormatted"
    val amountColor = if (transaction.isIncome) Color(0xFF2E7D32) else Color(0xFFC62828)
    val isLongText = transaction.title.length > 15
    val shortAmountText = remember(transaction.amount) {
        val amount = transaction.amount
        val formattedNum = when {
            amount >= 1_000_000 -> "${(amount / 1_000_000).toInt()}M"
            amount >= 100_000 -> "${(amount / 1_000).toInt()}K"
            else -> currencyFormat.format(amount)
        }
        if (transaction.isIncome) "+Rp $formattedNum" else "-Rp $formattedNum"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom // Mengatur posisi ke bagian bawah secara vertikal
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (transaction.isIncome) Icons.Default.ShoppingCart else Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.widthIn(max = 180.dp)
            ) {
                val isLongText = transaction.title.length > 19
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    modifier = if (isLongText) Modifier.basicMarquee() else Modifier
                )
                Text(
                    text = transaction.timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text(
            text = shortAmountText,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = amountColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionHistoryScreenPreview() {
    val dummyTransactions = listOf(
        TransactionEntity(
            id = 1,
            title = "Penjualan Teh Botol Sosro",
            timestamp = "10:30 WIB",
            amount = 14000.0,
            isIncome = true,
            dateGroup = "Hari Ini"
        ),
        TransactionEntity(
            id = 2,
            title = "Penjualan Beef Crowich",
            timestamp = "09:15 WIB",
            amount = 35000.0,
            isIncome = true,
            dateGroup = "Hari Ini"
        ),
        TransactionEntity(
            id = 3,
            title = "Belanja Stok Bahan Baku",
            timestamp = "14:20 WIB",
            amount = 150000.0,
            isIncome = false,
            dateGroup = "Kemarin"
        )
    )

    MaterialTheme {
        TransactionHistoryScreen(
            transactions = dummyTransactions, // Menggunakan emptyList()
            onBackClicked = {}
        )
    }
}