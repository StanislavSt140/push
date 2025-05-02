package com.example.push.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.push.data.ComplaintItem
import com.example.push.data.RetrofitClient
import com.example.push.data.UserPreferences
import com.example.push.navigation.Screen
import com.example.push.navigation.Screen.ComplaintsDetail
import com.example.push.ui.components.AppHeader
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ComplaintsScreen(navController: NavController) {
    val complaints = remember { mutableStateOf(emptyList<ComplaintItem>()) }
    val scope = rememberCoroutineScope()
    var isDialogOpen by remember { mutableStateOf(false) } // ⬅ Контролюємо відкриття попапа
    var userName by remember { mutableStateOf("") }
    var complaintText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val userRole = userPrefs.getUserName()
    val userClass = userPrefs.getUserClass()
// 📌 Додаємо форматування дати
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = RetrofitClient.api.getComplaints()
                if (response.status == "success") {
                    complaints.value = response.data!!
                }
            } catch (e: Exception) {
                Log.d("ComplaintsScreen", "Помилка завантаження скарг: ${e.message}")
            }
        }
    }

    AppHeader(navController, "Скарги") {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { isDialogOpen = true },
                    containerColor = Color.Red,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Filled.Warning, contentDescription = "Додати скаргу")
                }
            }
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(top = 76.dp)) {
                LazyColumn {
                    itemsIndexed(complaints.value) { index, complaint ->
                        ComplaintItemView(complaint) {
                            navController.navigate(ComplaintsDetail.createRoute(complaint.id))
                        }
                    }
                }
            }

            if (isDialogOpen) {
                AlertDialog(
                    onDismissRequest = { isDialogOpen = false },
                    title = { Text("Надіслати скаргу") },
                    text = {
                        Column {
                            TextField(
                                value = "$userRole - $userClass",
                                onValueChange = { userName = it },
                                label = { Text("Ваше ім'я") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextField(
                                value = complaintText,
                                onValueChange = { complaintText = it },
                                label = { Text("Текст скарги") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                scope.launch {
                                    try {
                                        val response = RetrofitClient.api.sendComplaint(userName, complaintText)
                                        if (response.status == "success") {
                                            complaints.value = complaints.value + ComplaintItem(
                                                complaints.value.size + 1,
                                                userName,
                                                complaintText,
                                                "Щойно",
                                                timestamp = LocalDateTime.now().format(formatter)

                                            )
                                            userName = ""
                                            complaintText = ""
                                            isDialogOpen = false
                                        }
                                    } catch (e: Exception) {
                                        Log.d("ComplaintsScreen", "Помилка надсилання скарги: ${e.message}")
                                    }
                                }
                            }
                        ) {
                            Text("Надіслати")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { isDialogOpen = false }) {
                            Text("Закрити")
                        }
                    }
                )
            }
        }
    }
}





@Composable
fun ComplaintItemView(complaint: ComplaintItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(complaint.content, style = MaterialTheme.typography.bodyMedium)
                Text("Автор: ${complaint.author}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text("Дата: ${complaint.timestamp}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            // 📌 **Статус скарги**
            if (complaint.description.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.Red, shape = RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Warning, contentDescription = "Не розглянуто", tint = Color.White)
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF03736A), shape = RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Розглянуто", tint = Color.White)
                }
            }
        }
    }
}