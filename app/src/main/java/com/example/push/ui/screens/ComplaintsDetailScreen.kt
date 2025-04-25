package com.example.push.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.push.data.ComplaintItem
import com.example.push.data.RetrofitClient
import com.example.push.ui.components.AppHeader
import kotlinx.coroutines.launch

@Composable
fun ComplaintsDetailScreen(complaintId: Int, navController: NavController) {
    val complaint = remember { mutableStateOf<ComplaintItem?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(complaintId) {
        scope.launch {
            try {
                val response = RetrofitClient.api.getComplaintDetails(complaintId)
                if (response.status == "success") {
                    complaint.value = response.data
                }
            } catch (e: Exception) {
                Log.d("ComplaintsDetailScreen", "Помилка завантаження деталей скарги: ${e.message}")
            }
        }
    }

    AppHeader(navController, "Деталі скарги") {
        Column(modifier = Modifier.fillMaxSize().padding(top = 86.dp)) {
            complaint.value?.let {
                Text("Автор: ${it.author}", style = MaterialTheme.typography.titleMedium)
                Text(it.content, style = MaterialTheme.typography.bodyMedium)
                Text("Дата: ${it.timestamp}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Назад")
                }
            } ?: Text("Завантаження...")
        }
    }
}