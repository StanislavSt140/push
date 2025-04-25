package com.example.push.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.push.data.RetrofitClient
import com.example.push.data.WishlistItem
import com.example.push.ui.components.AppHeader
import kotlinx.coroutines.launch

@Composable
fun WishlistDetailScreen(wishId: Int, navController: NavController) {
    val wish = remember { mutableStateOf<WishlistItem?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(wishId) {
        scope.launch {
            try {
                val response = RetrofitClient.api.getWishlistItem(wishId)
                if (response.status == "success") {
                    wish.value = response.data
                }
            } catch (e: Exception) {
                Log.d("WishlistDetailScreen", "Помилка завантаження побажання: ${e.message}")
            }
        }
    }

    AppHeader(navController, "Деталі побажання") {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            wish.value?.let {
                Text(it.content, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Дата створення: ${it.timestamp}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Назад")
                }
            } ?: Text("Завантаження...")
        }
    }
}