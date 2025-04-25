package com.example.push.market

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class CreateProductScreen(
    private val navController: NavHostController,
    private val marketApiService: MarketApiService,
    private val coroutineScope: CoroutineScope
) {

    @Composable
    fun RenderScreen() {
        var productName by remember { mutableStateOf("") }
        var productDescription by remember { mutableStateOf("") }
        var productPrice by remember { mutableStateOf("") }
        var productDiscountPrice by remember { mutableStateOf("") }
        var productImageUrl by remember { mutableStateOf("") }
        var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
        var categories by remember { mutableStateOf<List<CategoryItem>>(emptyList()) }
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            // Fetch categories on screen load
            coroutineScope.launch {
                try {
                    isLoading = true
                    val response = marketApiService.getCategories()
                    if (response.status == "success") {
                        categories = response.data ?: emptyList()
                    } else {
                        errorMessage = "Failed to load categories"
                    }
                } catch (e: Exception) {
                    errorMessage = "Error: ${e.message}"
                } finally {
                    isLoading = false
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = productName,
                onValueChange = { productName = it },
                label = { Text("Product Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = productDescription,
                onValueChange = { productDescription = it },
                label = { Text("Product Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = productPrice,
                onValueChange = { productPrice = it },
                label = { Text("Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = productDiscountPrice,
                onValueChange = { productDiscountPrice = it },
                label = { Text("Discount Price (Optional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = productImageUrl,
                onValueChange = { productImageUrl = it },
                label = { Text("Image URL") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            DropdownMenu(
                expanded = categories.isNotEmpty(),
                onDismissRequest = { /* No dismiss action */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(onClick = { selectedCategoryId = category.id }) {
                        Text(text = category.name)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                isLoading = true
                                errorMessage = ""
                                val price = productPrice.toDoubleOrNull()
                                val discountPrice = productDiscountPrice.toDoubleOrNull()
                                if (price == null || productName.isBlank() || selectedCategoryId == null) {
                                    errorMessage = "Please fill all required fields"
                                    isLoading = false
                                    return@launch
                                }

                                // Call the createProduct method in MarketApiService
                                val response = marketApiService.createProduct(
                                    title = productName,
                                    description = productDescription,
                                    price = price,
                                    discountPrice = discountPrice,
                                    imageUrl = productImageUrl,
                                    categoryId = selectedCategoryId!!
                                )
                                if (response.status == "success") {
                                    navController.popBackStack() // Navigate back on success
                                } else {
                                    errorMessage = "Failed to create product: ${response.data}"
                                }
                            } catch (e: Exception) {
                                errorMessage = "An error occurred: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Product")
                }
            }
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colors.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}