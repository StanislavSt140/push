package com.example.push.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.push.data.RetrofitClient
import com.example.push.data.RewardItem
import com.example.push.navigation.Screen
import com.example.push.ui.components.AppHeader
import kotlinx.coroutines.launch

@Composable
fun RewardsScreen(navController: NavController) {
    val rewards = remember { mutableStateOf(emptyList<RewardItem>()) }
    val scope = rememberCoroutineScope()
    val gradientColors = listOf(
        Color(0xFFFF4081),  // Pink
        Color(0xFF1E0F4F)   // Dark Purple
    )
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = RetrofitClient.api.getRewards()
                if (response.status == "success") {
                    rewards.value = response.data!!
                    rewards.value.forEach { Log.d("RewardItem", "Зображення: ${it.imageUrl}") }
                }
            } catch (e: Exception) {
                Log.d("RewardsScreen", "Помилка завантаження нагород: ${e.message}")
            }
        }
    }

    AppHeader(navController, "Система заохочення") {
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
        Column(modifier = Modifier.fillMaxSize().padding(top = 104.dp, start = 8.dp, end = 8.dp)) {
            LazyColumn {
                itemsIndexed(rewards.value) { index, reward ->
                    RewardItemView(reward) {
                        navController.navigate(Screen.RewardsDetail.createRoute(reward.id))
                    }
                }
            }
        }
        }
    }
}

@Composable
fun RewardItemView(reward: RewardItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(painter = rememberAsyncImagePainter(reward.imageUrl), contentDescription = "Зображення", modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(reward.title, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
