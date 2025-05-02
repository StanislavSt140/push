package com.example.push.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
        Column(modifier = Modifier.fillMaxSize().padding(top = 104.dp, start = 16.dp, end = 16.dp)) {
            reward.value?.let {
                Text(it.title, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Image(painter = rememberAsyncImagePainter(it.imageUrl), contentDescription = "Зображення нагороди", modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                Text(it.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Назад")
                }
            } ?: Text("Завантаження...")
        }
    }
}