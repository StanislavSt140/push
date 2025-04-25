package com.example.push.market

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.push.navigation.Screen
import com.example.push.ui.components.AppHeader
import kotlinx.coroutines.launch

@Composable
fun MarketScreen(navController: NavController) {
    val categories = remember { mutableStateOf(emptyList<CategoryItem>()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = RetrofitClient.marketApi.getCategories()
                if (response.status == "success") {
                    categories.value = response.data!!
                }
                Log.d("CategoryDetailScreen", "${response.status}: ${response.data} ")
            } catch (e: Exception) {
                Log.d("MarketScreen", "Помилка завантаження категорій: ${e.message}")
            }
        }
    }

    AppHeader(navController, "Креативний Маркет") {


        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 88.dp), // ⬅ Зменшуємо бокові відступи!
            horizontalArrangement = Arrangement.spacedBy(8.dp), // ⬅ Мінімальний простір між картками!
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories.value.size) { index ->
                val category = categories.value[index]
                CategoryItemView(category) {
                    navController.navigate(Screen.CategoryDetail.createRoute(category.id))
                }
            }
        }

    }
}

@Composable
fun CategoryItemView(category: CategoryItem, onClick: () -> Unit) {

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
                painter = rememberAsyncImagePainter(category.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer(modifier = Modifier.height(1.dp))

            Box(
                modifier = Modifier.fillMaxWidth().height(44.dp), // ⬅ Забезпечує повну ширину та фіксовану висоту
                contentAlignment = Alignment.Center // ⬅ Центрує текст і горизонтально, і вертикально
            ) {
                Text(
                    category.name,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center, // ⬅ Текст рівняється по центру
                    maxLines = 2
                )
            }


            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {



            }
        }
    }
}
