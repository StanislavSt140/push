package com.example.push.market

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.push.data.UserPreferences
import com.example.push.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

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

    val userPrefs = remember { UserPreferences(context) }
    val userId = userPrefs.getUserId()

    // Функція для конвертації URI → File
    fun getFileFromUri(context: Context, uri: Uri): File? {
        val file = File(context.cacheDir, "upload_image.jpg")
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Log.d("CreateProductScreen", "Файл створено: ${file.absolutePath}")
            file
        } catch (e: Exception) {
            Log.e("CreateProductScreen", "Помилка створення файлу: ${e.message}")
            null
        }
    }

    // Лаунчер для вибору зображення
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        productImageUri = uri
    }

    // Завантаження категорій
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
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Поле для вибору категорії
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isMenuExpanded = true } // ✅ Тепер працює на всій площі!
        ) {
            OutlinedTextField(
                value = categories.firstOrNull { it.id == selectedCategoryId }?.name ?: "Оберіть категорію",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true, // ✅ Не дозволяємо вводити вручну
                enabled = false, // ✅ Дозволяємо кліки тільки через `Box`
                label = { Text("Категорія") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Розгорнути меню"
                    )
                }
            )

            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false }
            ) {
                if (categories.isEmpty()) {
                    DropdownMenuItem(
                        onClick = {},
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
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Поля для введення даних
        TextField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Назва продукту") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = productDescription,
            onValueChange = { productDescription = it },
            label = { Text("Опис продукту") },
            modifier = Modifier.fillMaxWidth().height(150.dp),
            maxLines = 6
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
                containerColor = Color(0xFF03736A),
                contentColor = Color.White
            )
        ) {
            Text(text = "Вибрати Зображення")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка для створення продукту
        Button(
            onClick = {
                coroutineScope.launch {
                    if (productName.isEmpty() || productPrice.isEmpty() || selectedCategoryId == null || productImageUri == null) {
                        errorMessage = "Будь ласка, заповніть всі обов'язкові поля та виберіть зображення."
                        return@launch
                    }

                    try {
                        isLoading = true
                        val imageFile = getFileFromUri(context, productImageUri!!)
                        if (imageFile == null) {
                            errorMessage = "Помилка: Не вдалося створити файл із зображення."
                            return@launch
                        }

                        val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestBody)

                        val titlePart = productName.toRequestBody("text/plain".toMediaTypeOrNull())
                        val descriptionPart = productDescription.toRequestBody("text/plain".toMediaTypeOrNull())
                        val pricePart = productPrice.toRequestBody("text/plain".toMediaTypeOrNull())
                        val discountPart = productDiscountPrice.takeIf { it.isNotEmpty() }?.toRequestBody("text/plain".toMediaTypeOrNull())
                        val categoryIdPart = selectedCategoryId?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
                        val usId = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                        val response = marketApiService.createProduct(
                            title = titlePart,
                            description = descriptionPart,
                            price = pricePart,
                            discountPrice = discountPart,
                            categoryId = categoryIdPart!!,
                            image = imagePart,
                            userId = usId
                        )

                        if (response.status == "success") {
                            errorMessage = "Продукт успішно додано!"
                            navController.navigate(Screen.CategoryDetail.createRoute(
                                selectedCategoryId!!
                            )) // ✅ Переходимо назад
                        } else {
                            errorMessage = "Помилка: ${response.message ?: "Невідома помилка"}"
                        }
                    } catch (e: Exception) {
                        errorMessage = "Помилка: ${e.message}"
                        Log.e("CreateProductScreen", "Помилка під час завантаження зображення: ${e.message}")
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Додати")
        }

        // Відображення помилок
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp)
            )
        }
    }
}