package com.example.push.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import com.example.push.data.ProductItem
import com.example.push.data.RetrofitClient
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import androidx.navigation.compose.*
import coil.compose.rememberAsyncImagePainter
import com.example.push.ui.components.AppHeader
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip


@Composable
fun ShopDetailScreen(productId: Int, navController: NavController) {
    var product by remember { mutableStateOf<ProductItem?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(productId) {
        scope.launch {
            try {
                val response = RetrofitClient.api.getProducts()
                product = response.products.find { it.id == productId }
            } catch (e: Exception) {
                Log.d("ShopDetailScreen", "Error fetching product: ${e.message}")
            }
        }
    }

    AppHeader(navController, product?.name ?: "Деталі товару") {
        product?.let { item ->
            Column(
                modifier = Modifier.fillMaxSize().padding(top = 104.dp, start = 16.dp, end = 16.dp) // ⬅ Коригуємо відступи
            ) {
                Image(
                    painter = rememberAsyncImagePainter(item.imageUrl),
                    contentDescription = null,
                    modifier = Modifier.height(250.dp).fillMaxWidth().clip(RoundedCornerShape(12.dp)) // ⬅ Закруглюємо кути
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 🔥 Оформлення цін
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        if (item.discountPrice != null) {
                            Text(
                                "₴${item.discountPrice}",
                                style = MaterialTheme.typography.h6,
                                color = Color.Red
                            )
                            Text(
                                "₴${item.price}",
                                style = MaterialTheme.typography.body2.copy(textDecoration = TextDecoration.LineThrough),
                                color = Color.Gray
                            ) // ⬅ Перечеркнута стара ціна
                        } else {
                            Text("₴${item.price}", style = MaterialTheme.typography.h6)
                        }
                    }

                    RatingStars(rating = item.rating) // ⬅ Зірочки замість текстового рейтингу
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 📖 Опис товару з прокручуванням
                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Text(
                        item.description,
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.verticalScroll(rememberScrollState()) // ⬅ Додаємо прокручування!
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 🔙 Кнопка "Назад"
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 36.dp),
                    colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF03736A),  // ⬅ Змінюємо фон кнопки
                    contentColor = Color.White   // ⬅ Змінюємо колір тексту
                )

                ) {
                    Text("Назад")
                }
            }
        } ?: CircularProgressIndicator(modifier = Modifier.padding(16.dp))
    }
}