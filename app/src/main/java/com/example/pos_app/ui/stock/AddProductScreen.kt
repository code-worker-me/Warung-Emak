package com.example.pos_app.ui.stock

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TopAppBar
import com.example.pos_app.data.entity.CategoryEntity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pos_app.R
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    categoryList: List<CategoryEntity>,
    onBackClicked: () -> Unit,
    onSaveProduct: (name: String, price: Double, buyPrice: Double, quantity: Int, category: String, imageUri: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var buyPrice by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }

    val focusRequesterPrice = remember { FocusRequester() }
    val focusRequesterBuyPrice = remember { FocusRequester() }
    val focusRequesterStock = remember { FocusRequester() }
    val focusRequesterCategory = remember { FocusRequester() }

    // Launcher untuk Galeri
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) imageUri = uri
    }

    val context = LocalContext.current

    // Launcher untuk Kamera (Preview Bitmap)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        // Jika Anda ingin menyimpan bitmap ke URI lokal atau menanganinya langsung,
        // untuk sementara kita simpan sebagai penanda atau gunakan Uri hasil file if needed.
        if (bitmap != null) {
            // Simpan bitmap ke URI lokal cache
            val uri = saveBitmapToCache(context, bitmap)
            if (uri != null) {
                imageUri = uri // Masukkan ke state imageUri Anda
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tambah_produk)) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(270.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { showImageSourceDialog = true },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Upload Icon",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            // Input Nama Produk[cite: 2]
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Produk") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Words
                ),
                keyboardActions = KeyboardActions(onNext = { focusRequesterBuyPrice.requestFocus() }),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Input Harga Beli
            OutlinedTextField(
                value = buyPrice,
                onValueChange = { inputStr ->
                    val clean = inputStr.replace(Regex("[^\\d]"), "")
                    buyPrice = if (clean.isNotEmpty()) {
                        java.text.NumberFormat.getInstance(java.util.Locale("id", "ID")).format(clean.toLongOrNull() ?: 0L)
                    } else {
                        ""
                    }
                },
                label = { Text("Harga Beli") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequesterBuyPrice),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Words
                ),
                keyboardActions = KeyboardActions(onNext = { focusRequesterPrice.requestFocus() }),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Input Harga[cite: 2]
            OutlinedTextField(
                value = price,
                onValueChange = { inputStr ->
                    // Ambil hanya angka dari input
                    val clean = inputStr.replace(Regex("[^\\d]"), "")
                    price = if (clean.isNotEmpty()) {
                        val parsed = clean.toLongOrNull() ?: 0L
                        // Format ke ribuan dengan pemisah titik tanpa "Rp."
                        java.text.NumberFormat.getInstance(java.util.Locale("id", "ID")).format(parsed)
                    } else {
                        ""
                    }
                },
                label = { Text("Harga Jual") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequesterPrice),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Words
                ),
                keyboardActions = KeyboardActions(onNext = { focusRequesterStock.requestFocus() }),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Row Stok & Kategori[cite: 2]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stok") },
                    modifier = Modifier.weight(1f)
                        .focusRequester(focusRequesterStock),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusRequesterCategory.requestFocus() }),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategori") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .focusRequester(focusRequesterCategory),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        if (categoryList.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Belum ada kategori") },
                                onClick = { expanded = false }
                            )
                        } else {
                            categoryList.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.name) },
                                    onClick = {
                                        category = cat.name
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button Tambah Produk[cite: 2]
            Button(
                onClick = {
                    val cleanBuyPrice = buyPrice.replace(Regex("[^\\d]"), "")
                    val parsedBuyPrice = cleanBuyPrice.toDoubleOrNull() ?: 0.0

                    val cleanSellingPrice = price.replace(Regex("[^\\d]"), "")
                    val parsedSellingPrice = cleanSellingPrice.toDoubleOrNull() ?: 0.0

                    val parsedStock = stock.toIntOrNull() ?: 0
                    onSaveProduct(name, parsedSellingPrice, parsedBuyPrice, parsedStock, category, imageUri?.toString())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A453B) // Warna gelap sesuai referensi
                )
            ) {
                Text(text = "Tambah Produk", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }

    // Dialog Pilihan Sumber Gambar (Galeri / Kamera)
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Pilih Sumber Gambar") },
            text = { Text("Ambil foto dari kamera atau pilih dari galeri.") },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    galleryLauncher.launch("image/*")
                }) {
                    Text("Galeri")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    cameraLauncher.launch(null)
                }) {
                    Text("Kamera")
                }
            }
        )
    }
}

fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri? {
    return try {
        val file = File(context.cacheDir, "product_${System.currentTimeMillis()}.jpg")
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.flush()
        fos.close()
        Uri.fromFile(file)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}