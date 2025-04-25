package com.example.push.market

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.push.ui.components.AppHeader
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
        var isMenuExpanded by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
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

        AppHeader(navController, "Додати Товар") {
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
                Box {
                    OutlinedTextField(
                        value = categories.firstOrNull { it.id == selectedCategoryId }?.name ?: "Select Category",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = {
                            IconButton(onClick = { isMenuExpanded = !isMenuExpanded }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand Menu")
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(onClick = {
                                selectedCategoryId = category.id
                                isMenuExpanded = false
                            }) {
                                Text(text = category.name)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (productName.isNotEmpty() && selectedCategoryId != null) {
                                // Call API to create product
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Додати")
                }
            }
        }
    }
}