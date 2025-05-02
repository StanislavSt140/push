package com.example.push.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.push.data.NewsItem
import com.example.push.data.RetrofitClient
import com.example.push.navigation.Screen
import com.example.push.ui.components.AppHeader
import kotlinx.coroutines.launch

@Composable
fun NewsScreen(navController: NavController) {
    AppHeader(navController, "Push News") {
        var newsList by remember { mutableStateOf<List<NewsItem>>(emptyList()) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            scope.launch {
                val response = RetrofitClient.api.getNews()
                if (response.status == "success") {
                    newsList = response.news
                }
            }
        }

        Column(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
            Spacer(modifier = Modifier.height(24.dp)) // ⬅ Додаємо відступ заголовка
            Text(
                text = "📰 Push News",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color(0xFF03736A) // ⬅ Колір як у головному меню
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(newsList) { news ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = news.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                color = Color(0xFF03736A) // ⬅ Заголовок новини такого ж кольору
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Image(
                                painter = rememberAsyncImagePainter(news.imageUrl),
                                contentDescription = null,
                                modifier = Modifier.height(180.dp).fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    navController.navigate(
                                        Screen.NewsDetail.createRoute(
                                            news.id
                                        )
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF03736A
                                    )
                                ) // ⬅ Колір кнопки як у меню
                            ) {
                                Text("Детальніше", color = Color.White)
                            }
                        }
                    }
                }
            }
        }

    }

}