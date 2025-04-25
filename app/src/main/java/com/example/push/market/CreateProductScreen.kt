package com.example.push.market

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.example.push.data.ApiService

class CreateProductScreen(
    private val navController: NavHostController,
    private val apiService: ApiService,
    private val coroutineScope: CoroutineScope
) {

    @Composable
    fun RenderScreen() {
        var productName by remember { mutableStateOf("") }
        var productPrice by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }

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
                value = productPrice,
                onValueChange = { productPrice = it },
                label = { Text("Product Price") },
                modifier = Modifier.fillMaxWidth()
            )
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
                                if (price == null || productName.isBlank()) {
                                    errorMessage = "Invalid input"
                                    isLoading = false
                                    return@launch
                                }

                                // Call the addProduct method in ApiService
                                val response = apiService.addProduct(productName, price)
                                if (response.isSuccessful) {
                                    navController.popBackStack() // Navigate back
                                } else {
                                    errorMessage = "Failed to add product: ${response.errorBody()?.string()}"
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