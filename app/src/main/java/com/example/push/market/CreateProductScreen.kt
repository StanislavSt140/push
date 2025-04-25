package com.example.push.market

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.focus.onFocusChanged

@Composable
fun CreateProductScreen(
    navController: NavHostController,
    marketApiService: MarketApiService,
    coroutineScope: CoroutineScope
) {
    var productName by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var productDiscountPrice by remember { mutableStateOf("") }
    var productImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    var categories by remember { mutableStateOf<List<CategoryItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var isMenuExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current

    // 🔹 Функція для конвертації URI → File
    fun getFileFromUri(context: Context, uri: Uri): File? {
        val file = File(context.cacheDir, "upload_image.jpg")
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            return file
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    // 🔹 Лаунчер для вибору зображення
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        productImageUri = uri
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                isLoading = true
                val response = marketApiService.getCategories()
                if (response.status == "success") {
                    categories = response.data ?: emptyList()
                } else {
                    errorMessage = "Не вдалося завантажити категорії"
                }
            } catch (e: Exception) {
                errorMessage = "Помилка: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { isMenuExpanded = false }
        ) {
            if (categories.isEmpty()) {
                DropdownMenuItem(
                    onClick = { },
                    text = { Text("Немає доступних категорій") }
                )
            } else {
                categories.forEach { category ->
                    DropdownMenuItem(
                        onClick = {
                            selectedCategoryId = category.id
                            isMenuExpanded = false
                        },
                        text = { Text(category.name) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Поля вводу
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

        // Вибране зображення
        productImageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Вибране зображення",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(200.dp).padding(vertical = 8.dp)
            )
        } ?: Text("Зображення не вибрано", modifier = Modifier.align(Alignment.CenterHorizontally))

        // Кнопка вибору зображення
        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF03736A), // Колір фону
                contentColor = Color.White          // Колір тексту
            )
        ) {
            Text(text = "Вибрати Зображення")
        }
        Spacer(modifier = Modifier.height(16.dp))






        // Кнопка додавання продукту
        Button(
            onClick = {
                coroutineScope.launch {
                    Log.d("CreateProductScreen", "productName: $productName, productPrice: $productPrice, selectedCategoryId: $selectedCategoryId, productImageUri: $productImageUri()")

                    if (productName.isEmpty() || productPrice.isEmpty() || selectedCategoryId == null || productImageUri == null) {
                        errorMessage = "Будь ласка, заповніть всі обов'язкові поля"
                    } else {
                        try {
                            isLoading = true

                            // 🔹 Отримання `File` зі `Uri`
                            val imageFile = getFileFromUri(context, productImageUri!!)
                            if (imageFile == null) {
                                errorMessage = "Помилка: Не вдалося отримати файл зображення!"
                                isLoading = false
                                return@launch
                            }

                            val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestBody)

                            // 🔹 Перетворення текстових даних у `RequestBody`
                            val titlePart = productName.toRequestBody("text/plain".toMediaTypeOrNull())
                            val descriptionPart = productDescription.toRequestBody("text/plain".toMediaTypeOrNull())
                            val pricePart = productPrice.toRequestBody("text/plain".toMediaTypeOrNull())
                            val discountPart = productDiscountPrice.takeIf { it.isNotEmpty() }?.toRequestBody("text/plain".toMediaTypeOrNull())
                            val categoryIdPart = selectedCategoryId?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

                            // 🔹 Відправка даних на сервер
                            val response = marketApiService.createProduct(
                                title = titlePart,
                                description = descriptionPart,
                                price = pricePart,
                                discountPrice = discountPart,
                                categoryId = categoryIdPart!!,
                                image = imagePart
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
                            //    errorMessage = "Помилка: ${response.message}"
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

        // Відображення помилок
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp))
        }
    }
}