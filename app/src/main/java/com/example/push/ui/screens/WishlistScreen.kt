package com.example.push.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.push.data.RetrofitClient
import com.example.push.data.WishlistCategory
import com.example.push.navigation.Screen
import com.example.push.ui.components.AppHeader
import kotlinx.coroutines.launch

@Composable
fun WishlistScreen(navController: NavController) {
    val categories = remember { mutableStateOf(emptyList<WishlistCategory>()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = RetrofitClient.api.getWishlistCategories()
                if (response.status == "success") {
                    categories.value = response.data!!
                }
            } catch (e: Exception) {
                Log.d("WishlistScreen", "Помилка завантаження категорій: ${e.message}")
            }
        }
    }

    AppHeader(navController, "Скарбничка побажань") {
        Column(modifier = Modifier.fillMaxSize().padding(0.dp).padding(top = 72.dp, start = 0.dp, end = 0.dp)) {

            LazyColumn {
                itemsIndexed(categories.value) { index, category ->
                    WishlistCategoryItem(category) {
                        navController.navigate(Screen.WishlistCategory.createRoute(category.id))
                    }
                }
            }
        }
    }
}

@Composable
fun WishlistCategoryItem(category: WishlistCategory, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = rememberAsyncImagePainter(category.imageUrl),
                contentDescription = null,
                modifier = Modifier.height(180.dp).fillMaxWidth()
            )
            Text(
                category.name,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}