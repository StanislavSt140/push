package com.example.push.market

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
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
        var productImageUri by remember { mutableStateOf<Uri?>(null) } // Змінна для URI зображення
        var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
        var categories by remember { mutableStateOf<List<CategoryItem>>(emptyList()) }
        var isLoading by remember { mutableStateOf(false) }
        var isMenuExpanded by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }

        // Лаунчер для вибору зображення
        val imagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            productImageUri = uri
        }

        // Логіка завантаження категорій
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
                // Поля вводу для даних продукту
                TextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Назва продукту") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = productDescription,
                    onValueChange = { productDescription = it },
                    label = { Text("Опис продукту") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = productPrice,
                    onValueChange = { productPrice = it },
                    label = { Text("Ціна") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = productDiscountPrice,
                    onValueChange = { productDiscountPrice = it },
                    label = { Text("Знижка (опційно)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Відображення вибраного зображення
                productImageUri?.let { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Вибране зображення",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(vertical = 8.dp)
                    )
                } ?: Text(
                    text = "Зображення не вибрано",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                // Кнопка для вибору зображення
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF03736A), // Колір фону
                        contentColor = Color.White          // Колір тексту
                    )
                ) {
                    Text(text = "Вибрати Зображення")
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Вибір категорії продукту
                Box {
                    OutlinedTextField(
                        value = categories.firstOrNull { it.id == selectedCategoryId }?.name ?: "Оберіть категорію",
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isMenuExpanded = !isMenuExpanded }, // Додаємо клікабельність до всього поля
                        readOnly = true,
                        label = { Text("Категорія") },
                        trailingIcon = {
                            IconButton(onClick = { isMenuExpanded = !isMenuExpanded }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Розгорнути меню")
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (categories.isEmpty()) {
                            DropdownMenuItem(onClick = {}) {
                                Text("Немає доступних категорій")
                            }
                        } else {
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
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Кнопка додавання продукту
                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (productName.isEmpty() || productPrice.isEmpty() || selectedCategoryId == null || productImageUri == null) {
                                errorMessage = "Будь ласка, заповніть всі обов'язкові поля"
                            } else {
                                try {
                                    isLoading = true

                                    val imageFile = File(productImageUri!!.path!!) // ⬅ Отримуємо файл із URI
                                    val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                                    val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestBody)

                                    val response = marketApiService.createProduct(
                                        title = productName,
                                        description = productDescription,
                                        price = productPrice.toDouble(),
                                        discountPrice = productDiscountPrice.takeIf { it.isNotEmpty() }?.toDouble(),
                                        imageUrl = imagePart, // ⬅ Відправляємо зображення
                                        categoryId = selectedCategoryId!!
                                    )

                                    if (response.status == "success") {
                                        errorMessage = "Продукт додано успішно!"
                                        productName = ""
                                        productDescription = ""
                                        productPrice = ""
                                        productDiscountPrice = ""
                                        productImageUri = null
                                        selectedCategoryId = null
                                    } else {
                                        errorMessage = "Помилка: ${response.message}"
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Помилка: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Додати")
                }

                // Відображення повідомлення про помилку
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Індикатор завантаження
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 16.dp)
                    )
                }
            }
        }
    }
}