package com.example.push.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.push.data.ForumCategory
import com.example.push.data.RetrofitClient
import com.example.push.navigation.Screen
import com.example.push.ui.components.AppHeader
import kotlinx.coroutines.launch

@Composable
fun ForumScreen(navController: NavController) {
    val categories = remember { mutableStateOf(emptyList<ForumCategory>()) }
    val scope = rememberCoroutineScope()
    var isDialogOpen by remember { mutableStateOf(false) } // ⬅ Контролюємо попап
    var categoryName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = RetrofitClient.api.getForumCategories()
                if (response.status == "success") {
                    categories.value = response.data!!
                }
            } catch (e: Exception) {
                Log.d("ForumScreen", "Помилка завантаження категорій: ${e.message}")
            }
        }
    }

    AppHeader(navController, "Учнівський форум") {
        Scaffold(
            floatingActionButton = { // ⬅ Додаємо кнопку створення теми
                FloatingActionButton(
                    onClick = { isDialogOpen = true },
                    containerColor = Color(0xFF03736A),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Створити тему")
                }
            }
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(top = 56.dp, start = 0.dp, end = 0.dp)) {
                LazyColumn {
                    items(categories.value) { category ->
                        ForumCategoryItem(category) { navController.navigate(Screen.ForumDetail.createRoute(category.id)) }
                    }
                }
            }

            // 📌 **Попап для створення теми**
            if (isDialogOpen) {
                AlertDialog(
                    onDismissRequest = { isDialogOpen = false },
                    title = { Text("Створити нову тему") },
                    text = {
                        Column {
                            TextField(
                                value = categoryName,
                                onValueChange = { categoryName = it },
                                label = { Text("Назва теми") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                scope.launch {
                                    try {
                                        val response = RetrofitClient.api.createForumCategory(categoryName)
                                        if (response.status == "success") {
                                            categories.value = categories.value + ForumCategory(
                                                categories.value.size + 1, categoryName, "Ти",
                                                description = "Нова тема"
                                            )
                                            categoryName = ""
                                            isDialogOpen = false
                                        }
                                    } catch (e: Exception) {
                                        Log.d("ForumScreen", "Помилка створення теми: ${e.message}")
                                    }
                                }
                            }
                        ) {
                            Text("Створити")
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
fun ForumCategoryItem(category: ForumCategory, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(category.title, style = MaterialTheme.typography.titleMedium)
            Text("Автор: ${category.author}", style = MaterialTheme.typography.bodySmall, color = Color.Gray) // ⬅ Додаємо автора
            Spacer(modifier = Modifier.height(8.dp))
            Text(category.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun CreateForumCategory(navController: NavController) {
    var categoryName by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Створити нову категорію", style = MaterialTheme.typography.headlineMedium)

        TextField(
            value = categoryName,
            onValueChange = { categoryName = it },
            label = { Text("Назва категорії") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                scope.launch {
                    try {
                        val response = RetrofitClient.api.createForumCategory(categoryName)
                        if (response.status == "success") {
                            navController.popBackStack()
                        }
                    } catch (e: Exception) {
                        Log.d("CreateForumCategory", "Помилка створення категорії: ${e.message}")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Створити")
        }
    }
}