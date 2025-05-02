package com.example.push.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.push.data.RetrofitClient
import com.example.push.ui.components.AppHeader
import kotlinx.coroutines.launch

@Composable
fun ClassStudentsScreen(navController: NavController, className: String) {
    val scope = rememberCoroutineScope()
    var students by remember { mutableStateOf(listOf<com.example.push.data.UserItem>()) }
    var isLoading by remember { mutableStateOf(false) }
    var popupMessage by remember { mutableStateOf("") }

    LaunchedEffect(className) {
        scope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.api.getUsersByClass(className)
                if (response.status == "success" && response.data != null) {
                    students = response.data
                } else {
                    popupMessage = "Не вдалося отримати список учнів"
                }
            } catch (e: Exception) {
                popupMessage = "Помилка: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    AppHeader(navController, "Учні класу $className") {
        Box(modifier = Modifier.fillMaxSize().padding(top = 104.dp)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                if (students.isEmpty()) {
                    Text(
                        text = "В цьому класі немає учнів.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        students.forEach { student ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = student.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = student.phone,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (popupMessage.isNotEmpty()) {
                AlertDialog(
                    onDismissRequest = { popupMessage = "" },
                    title = { Text("Повідомлення") },
                    text = { Text(popupMessage) },
                    confirmButton = {
                        Button(onClick = { popupMessage = "" }) { Text("OK") }
                    }
                )
            }
        }
    }
}
