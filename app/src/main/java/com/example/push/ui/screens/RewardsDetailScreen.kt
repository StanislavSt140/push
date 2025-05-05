package com.example.push.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.push.data.RetrofitClient
import com.example.push.data.RewardItem
import com.example.push.ui.components.AppHeader
import kotlinx.coroutines.launch

@Composable
fun RewardsDetailScreen(rewardId: Int, navController: NavController) {
    val reward = remember { mutableStateOf<RewardItem?>(null) }
    val scope = rememberCoroutineScope()
    val gradientColors = listOf(
        Color(0xFFFF4081),  // Pink
        Color(0xFF1E0F4F)   // Dark Purple
    )
    LaunchedEffect(rewardId) {
        scope.launch {
            try {
                val response = RetrofitClient.api.getRewardDetails(rewardId)
                if (response.status == "success") {
                    reward.value = response.data
                }
            } catch (e: Exception) {
                Log.d("RewardsDetailScreen", "Помилка завантаження деталей нагороди: ${e.message}")
            }
        }
    }

    AppHeader(navController, "Деталі нагороди") {
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
        Column(modifier = Modifier.fillMaxSize().padding(top = 104.dp, start = 16.dp, end = 16.dp)) {
            reward.value?.let {
                Text(it.title, style = MaterialTheme.typography.headlineMedium, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Image(painter = rememberAsyncImagePainter(it.imageUrl), contentDescription = "Зображення нагороди", modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                Text(it.description, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF4081)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(48.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(24.dp)
                        )

                ) {
                    Text("Назад")
                }
            } ?: Text("Завантаження...", color = Color.White)
        }
        }
    }
}