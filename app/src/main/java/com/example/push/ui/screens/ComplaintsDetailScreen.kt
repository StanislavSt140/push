package com.example.push.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.push.data.ComplaintItem
import com.example.push.data.RetrofitClient
import com.example.push.ui.components.AppHeader
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.example.push.data.UserPreferences

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ComplaintsDetailScreen(complaintId: Int, navController: NavController) {
    val complaint = remember { mutableStateOf<ComplaintItem?>(null) }
    val scope = rememberCoroutineScope()

    // ⬇ Отримуємо роль користувача
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val userRole = userPrefs.getUserRole()

    var replyText by remember { mutableStateOf("") } // ✅ Зберігаємо відповідь адміністратора
    var isLoading by remember { mutableStateOf(false) } // ✅ Стан завантаження
    var popupMessage by remember { mutableStateOf("") } // ✅ Для попап-повідомлень

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
    AppHeader(navController, "Скарги") {
    Column(modifier = Modifier.fillMaxSize().padding(top = 104.dp)) {
        complaint.value?.let {
            Text("Автор: ${it.author}", style = MaterialTheme.typography.titleMedium)
            Text(it.description, style = MaterialTheme.typography.bodyMedium)
            Text(
                "Дата: ${it.timestamp}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))

            // ✅ Якщо користувач — адмін, показуємо поле для відповіді
            if (userRole == "admin") {
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    label = { Text("Відповідь адміністратора") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // ✅ Кнопка "Відправити" з перевіркою
                Button(
                    onClick = {
                        if (replyText.isNotEmpty()) {
                            scope.launch {
                                try {
                                    isLoading = true
                                    val response = RetrofitClient.api.sendComplaintReply(
                                        complaintId,
                                        replyText
                                    )
                                    if (response.status == "success") {
                                        popupMessage = "✅ Відповідь успішно відправлена!"
                                        replyText = ""
                                    } else {
                                        popupMessage =
                                            "❌ Помилка: ${response.message ?: "Невідома помилка"}"
                                    }
                                } catch (e: Exception) {
                                    popupMessage = "❌ Помилка: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        } else {
                            popupMessage = "⚠ Будь ласка, введіть відповідь перед відправкою."
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Відправити відповідь")
                }

                // ✅ Завантаження під кнопкою
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }

            Button(onClick = { navController.popBackStack() }) {
                Text("Назад")
            }
        } ?: Text("Завантаження...")
    }

    // ✅ Попап-повідомлення
    if (popupMessage.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { popupMessage = "" },
            title = { Text("Повідомлення") },
            text = { Text(popupMessage) },
            confirmButton = {
                Button(onClick = { popupMessage = "" }) {
                    Text("OK")
                }
            }
        )
    }
}
}
