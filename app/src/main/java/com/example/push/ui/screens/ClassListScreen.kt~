package com.example.push.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.push.ui.components.AppHeader
import com.example.push.data.RetrofitClient
import com.example.push.data.UserPreferences
import com.example.push.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun ClassListScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    var classList by remember { mutableStateOf(listOf<String>()) }
    var isLoading by remember { mutableStateOf(false) }
    var popupMessage by remember { mutableStateOf("") }
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val userRole = userPrefs.getUserRole()
    // Завантаження класів (унікальні значення)
    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.api.getAllUsers()
                if (response.status == "success" && response.data != null) {
                    classList = response.data.mapNotNull { it.className }.filter { it.isNotBlank() }.distinct().sorted()
                } else {
                    popupMessage = "Не вдалося отримати список класів"
                }
            } catch (e: Exception) {
                popupMessage = "Помилка: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    AppHeader(navController, "Класи учнів") {
        Box(modifier = Modifier.fillMaxSize().padding(top = 86.dp)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Box(modifier = Modifier.fillMaxSize()) { // ✅ Окремий контейнер для списку
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(classList) { className ->
                            ClassDoorItem(className = className) {
                                navController.navigate("class_students/$className")
                            }
                        }
                    }
                }
            }

            // ✅ Переконуємося, що кнопка "Додати учня" **завжди** поверх інших елементів
            if (userRole == "admin") {
                FloatingActionButton(
                    onClick = {
                        Log.d("ClassListScreen", "✅ Кнопка натиснута")
                        navController.navigate(Screen.AddStudent.route)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp, bottom = 36.dp)
                        .zIndex(1f), // ✅ Піднімаємо кнопку поверх `LazyVerticalGrid`
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Додати учня")
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

@Composable
fun ClassDoorItem(className: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(0.7f)
            .clickable { onClick() }
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF9E9E9E), Color(0xFF616161))
                ),
                shape = MaterialTheme.shapes.medium
            )
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        // Двері
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF795548), shape = MaterialTheme.shapes.medium)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Табличка класу
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(Color(0xFFFFF176), shape = MaterialTheme.shapes.small)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = className,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723)
                )
            }
        }
    }
}
