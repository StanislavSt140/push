package com.example.push.market

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.push.data.UserPreferences
import com.example.push.navigation.Screen
import com.example.push.ui.components.AppHeader
import com.example.push.ui.screens.RatingStars
import kotlinx.coroutines.launch


val categoryImages = mapOf(
   1 to "https://kuz.ua/media/catalog/product/cache/ccdd932b0895f683a3be943ad3d524b0/5/1/5130482.jpg",
   2 to "https://img.freepik.com/free-vector/key-bunch-with-keychain-metal-ring_107791-626.jpg",
   3 to "https://cdn.pixabay.com/photo/2023/09/23/14/22/dahlia-8271071_640.jpg",
   4 to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTQ-jwoehuZnKQkxOqG0V0L5LvrhoJdPehNjA&s",
   5 to "https://magicmeow.com.ua/wp-content/uploads/2024/01/IMG_2816-scaled.jpg",
   6 to "https://static.tildacdn.com/tild3364-3237-4538-a331-336530333966/popart1.jpg",
   7 to "https://kanckapital.com.ua/content/uploads/images/stationery.jpg",
   8 to "https://i.ukrfashion.com.ua/images/products/38f5bd702621c5dde590d1585891c4de.jpg",
   9 to "https://st2.depositphotos.com/7325936/11994/v/600/depositphotos_119940620-stock-illustration-eco-natural-product-wooden-labels.jpg"
)



@Composable
fun CategoryDetailScreen(categoryId: Int, navController: NavController) {
    val products = remember { mutableStateOf(emptyList<ProductItem>()) }
    val scope = rememberCoroutineScope()

    // ⬇ Отримуємо роль користувача
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val userRole = userPrefs.getUserRole()

    // ⬇ Стан для пошуку та фільтрації
    val searchQuery = remember { mutableStateOf("") }
    val selectedFilter = remember { mutableStateOf("Всі") }
    var isFilterMenuExpanded by remember { mutableStateOf(false) }

    // ⬇ Завантаження товарів
    LaunchedEffect(categoryId) {
        scope.launch {
            try {
                val response = RetrofitClient.marketApi.getProducts(categoryId)
                if (response.status == "success" && response.data != null) {
                    products.value = response.data!!
                } else {
                    Log.d("CategoryDetailScreen", "Помилка: порожній список товарів ${categoryId}")
                }
            } catch (e: Exception) {
                Log.d("CategoryDetailScreen", "Помилка завантаження товарів: ${e.message}")
            }
        }
    }

    AppHeader(navController, "Категорія") {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // 🔹 Відображення зображення категорії
            val categoryImageUrl = categoryImages[categoryId] ?: "https://example.com/default_image.jpg"
            Image(
                painter = rememberAsyncImagePainter(categoryImageUrl),
                contentDescription = "Зображення категорії",
                modifier = Modifier.fillMaxWidth().padding(top = 68.dp).height(150.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))


            // 🔹 Меню фільтрів



            var isFiltersVisible by remember { mutableStateOf(false) }

            Button(
                onClick = { isFiltersVisible = !isFiltersVisible }, // ✅ Відкриваємо або закриваємо фільтри
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isFiltersVisible) "Закрити фільтри" else "Фільтри")
            }

            AnimatedVisibility(visible = isFiltersVisible) { // ✅ Анімація показу фільтрів
                Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    // 🔹 Поле пошуку
                    OutlinedTextField(
                        value = searchQuery.value,
                        onValueChange = { searchQuery.value = it },
                        label = { Text("Пошук товарів") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { Icon(Icons.Default.Search, contentDescription = "Пошук") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 🔹 Меню вибору фільтра

                    Box(modifier = Modifier.fillMaxWidth().clickable { isFilterMenuExpanded = true }) {
                        OutlinedTextField(
                            value = selectedFilter.value,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = { Text("Фільтр") },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Розгорнути меню")
                            }
                        )

                        DropdownMenu(
                            expanded = isFilterMenuExpanded,
                            onDismissRequest = { isFilterMenuExpanded = false }
                        ) {
                            listOf("Всі", "Ціна: зростаюча", "Ціна: спадна", "За рейтингом").forEach { filter ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedFilter.value = filter
                                        isFilterMenuExpanded = false
                                    },
                                    text = { Text(filter) }
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Фільтрація товарів
            val filteredProducts = remember {
                derivedStateOf {
                    val sortedList = products.value.filter { product ->
                        product.title.contains(searchQuery.value, ignoreCase = true)
                    }

                    when (selectedFilter.value) {
                        "Ціна: зростаюча" -> sortedList.sortedBy { it.discountPrice?.takeIf { it > 0 } ?: it.price }
                        "Ціна: спадна" -> sortedList.sortedByDescending { it.discountPrice?.takeIf { it > 0 } ?: it.price }
                        "За рейтингом" -> sortedList.sortedByDescending { it.rating }
                        else -> sortedList
                    }
                }
            }

            // 🔹 Відображення товарів
            if (selectedFilter.value != "Всі") {
                Button(
                    onClick = { selectedFilter.value = "Всі" },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    border = BorderStroke(1.dp, Color.Gray),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    elevation = ButtonDefaults.buttonElevation(2.dp) // ✅ Робимо кнопку "плоскою"
                ) {
                    Text(text = "Очистити фільтр: ${selectedFilter.value}", color = Color.DarkGray)
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 0.dp, vertical = 40.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredProducts.value.size) { index ->
                    val product = filteredProducts.value[index]

                    ProductItemView(
                        product,
                        onClick = { navController.navigate(Screen.ProductDetail.createRoute(product.id)) },
                        onEdit = { if (userRole == "admin") Log.d("Edit", "Редагування товару ${product.id}") },
                        onDelete = {
                            scope.launch {
                                val response = RetrofitClient.marketApi.deleteProduct(product.id)
                                if (response.status == "success") {
                                    products.value = products.value.filter { it.id != product.id }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductItemView(
    product: ProductItem,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val userRole = userPrefs.getUserRole()

    Card(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(0.dp)
            .clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // 🔹 Додаємо зображення товару
            Image(
                painter = rememberAsyncImagePainter(product.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
            )

            // 🔹 Якщо є знижка, додаємо лейбл у лівий верхній кут
            if (product.discountPrice != null && product.discountPrice > 0) {
                val discountPercentage = (((product.price - product.discountPrice) / product.price) * 100).toInt() // ✅ Обчислюємо %

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart) // ✅ Вирівнюємо в лівому верхньому куті
                        .padding(8.dp)
                        .background(Color.Red, shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "-$discountPercentage%",
                        color = Color.White,
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }

            if (userRole == "admin") {
                // 🔹 Переміщуємо кнопки у правий верхній кут
                Row(
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { onEdit() },
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), shape = CircleShape)
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Редагувати", tint = Color.Blue)
                    }

                    IconButton(
                        onClick = { onDelete() },
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), shape = CircleShape)
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Видалити", tint = Color.Red)
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(10.dp)
        ) {
            Text(
                text = product.title,
                style = TextStyle(fontSize = 16.sp),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally) // ✅ Центруємо текст!
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (product.discountPrice != null && product.discountPrice > 0) {
                        Text(
                            text = "₴${product.discountPrice.toInt()}",
                            style = TextStyle(fontSize = 16.sp),
                            color = Color.Red
                        )
                        Text(
                            text = "₴${product.price.toInt()}",
                            style = TextStyle(fontSize = 14.sp, textDecoration = TextDecoration.LineThrough),
                            color = Color.Gray
                        )
                    } else {
                        Text(
                            text = "₴${product.price.toInt()}",
                            style = TextStyle(fontSize = 16.sp),
                            color = Color.Black
                        )
                    }
                }

                // 🔹 Іконка "Додати в кошик" справа
                IconButton(onClick = { /* TODO: Реалізуємо функціонал додавання */ }) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = "Додати в кошик",
                        tint = Color(0xFF03736A)
                    )
                }

            }
            RatingStarsShop(rating = product.rating)
        }
    }
}

@Composable
fun RatingStarsShop(rating: Int) {
    Row {
        repeat(rating) { index ->
            Icon(
                imageVector = if (index < rating.toInt()) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = Color(0xFFFF0033),
                modifier = Modifier.size(15.dp)
            )
        }
    }
}