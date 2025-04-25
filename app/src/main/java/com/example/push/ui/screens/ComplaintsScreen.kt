package com.example.push.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.push.data.ComplaintItem
import com.example.push.data.RetrofitClient
import com.example.push.navigation.Screen
import com.example.push.navigation.Screen.ComplaintsDetail
import com.example.push.ui.components.AppHeader
import kotlinx.coroutines.launch

@Composable
fun ComplaintsScreen(navController: NavController) {
    val complaints = remember { mutableStateOf(emptyList<ComplaintItem>()) }
    val scope = rememberCoroutineScope()
    var isDialogOpen by remember { mutableStateOf(false) } // ⬅ Контролюємо відкриття попапа
    var userName by remember { mutableStateOf("") }
    var complaintText by remember { mutableStateOf("") }

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
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(top = 56.dp)) {
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
                                value = userName,
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
                                            complaints.value = complaints.value + ComplaintItem(complaints.value.size + 1, userName, complaintText, "Щойно")
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
        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onClick() }, // ⬅ Додаємо `clickable`
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Автор: ${complaint.author}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(complaint.content, style = MaterialTheme.typography.bodyMedium)
            Text("Дата: ${complaint.timestamp}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}