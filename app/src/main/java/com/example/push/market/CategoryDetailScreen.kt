package com.example.push.market

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.push.navigation.Screen
import com.example.push.ui.components.AppHeader
import com.example.push.ui.screens.ProductCard
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

    LaunchedEffect(categoryId) {
        scope.launch {
            try {
                val response = RetrofitClient.marketApi.getProducts(categoryId)
                if (response.status == "success" && response.data != null) {
                    products.value = response.data!!
                        //   Log.d("CategoryDetailScreen", response.data.toString())
                } else {
                    Log.d("CategoryDetailScreen", "Помилка: порожній список товарів ${categoryId}")
                }

            } catch (e: Exception) {

                Log.d("CategoryDetailScreen", "Помилка завантаження товарів: ${e.message}")
            }
        }
    }

    AppHeader(navController, "Категорія") {
        Column {
            // ⬇️ Відображення зображення категорії
            val categoryImageUrl = categoryImages[categoryId] ?: "https://example.com/default_image.jpg"
            Image(
                painter = rememberAsyncImagePainter(categoryImageUrl),
                contentDescription = "Зображення категорії",
                modifier = Modifier.fillMaxWidth().padding(top = 68.dp).height(200.dp)
            )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp), // ⬅ Зменшуємо бокові відступи!
                    horizontalArrangement = Arrangement.spacedBy(8.dp), // ⬅ Мінімальний простір між картками!
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(products.value.size) { index ->
                        val product = products.value[index]
                        ProductItemView(product) {
                            navController.navigate(Screen.ProductDetail.createRoute(product.id))
                        }
                    }
                }

        }
    }
}

@Composable
fun ProductItemView(product: ProductItem, onClick: () -> Unit) {
    Log.d("CategoryDetailScreen", product.toString())
    Card(
        modifier = Modifier
            .fillMaxWidth(0.95f) // ⬅ Картка займає майже всю доступну ширину!
            .padding(0.dp) // ⬅ Мінімальні відступи між картками!
            .clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
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
                product.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.height(44.dp),
                maxLines = 2
            )


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
