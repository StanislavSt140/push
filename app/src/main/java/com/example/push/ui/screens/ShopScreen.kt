package com.example.push.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.push.data.ProductItem
import com.example.push.data.RetrofitClient
import com.example.push.ui.components.AppHeader
import kotlinx.coroutines.launch

@Composable
fun ShopScreen(navController: NavController) {
    val products = remember { mutableStateOf(emptyList<ProductItem>()) }
    val scope = rememberCoroutineScope()
    val gradientColors = listOf(
        Color(0xFFFF4081),  // Pink
        Color(0xFF1E0F4F)   // Dark Purple
    )

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = RetrofitClient.api.getProducts()
                if (response.status == "success") {
                    products.value = response.products
                }
            } catch (e: Exception) {
                // Логування або показ повідомлення про помилку
            }
        }
    }

    AppHeader(navController, "Push School Shop") {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = gradientColors,
                        start = androidx.compose.ui.geometry.Offset(
                            Float.POSITIVE_INFINITY,
                            Float.POSITIVE_INFINITY
                        ), // bottom-right
                        end = androidx.compose.ui.geometry.Offset(0f, 0f) // top-left
                    )
                )
        ) {
        Column(modifier = Modifier.fillMaxSize()
            .padding(top = 104.dp, start = 16.dp, end = 16.dp, bottom = 40.dp)
            .background(
                Brush.linearGradient(
                    colors = gradientColors,
                    start = androidx.compose.ui.geometry.Offset(
                        Float.POSITIVE_INFINITY,
                        Float.POSITIVE_INFINITY
                    ), // bottom-right
                    end = androidx.compose.ui.geometry.Offset(0f, 0f) // top-left
                )
            )
        ) { // ⬅ Менші відступи!
            if (products.value.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp), // ⬅ Зменшуємо бокові відступи!
                    horizontalArrangement = Arrangement.spacedBy(8.dp), // ⬅ Мінімальний простір між картками!
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(products.value.size) { index ->
                        val product = products.value[index]
                        ProductCard(product) { navController.navigate("shopDetail/${product.id}") }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.padding(36.dp))
                }
            }
        }
        }
    }
}

@Composable
fun ProductCard(product: ProductItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.95f) // ⬅ Картка займає майже всю доступну ширину!
            .padding(0.dp) // ⬅ Мінімальні відступи між картками!
            .clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Image(
                painter = rememberAsyncImagePainter(product.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer(modifier = Modifier.height(1.dp))

            Text(
                product.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.height(44.dp),
                maxLines = 2
            )

            RatingStars(rating = product.rating)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (product.discountPrice != null) {
                        Text(
                            "₴${product.discountPrice}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Red
                        )
                        Text(
                            "₴${product.price}",
                            style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.LineThrough),
                            color = Color.Gray
                        )
                    } else {
                        Text("₴${product.price}", style = MaterialTheme.typography.bodyLarge)
                    }
                }

                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = "Додати в кошик",
                        tint = Color(0xFF03736A)
                    )
                }
            }
        }
    }
}
@Composable
fun RatingStars(rating: Float) {
    Row {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < rating.toInt()) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = Color(0xFFFF0033),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}