package com.example.push.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.push.data.ForumPost
import com.example.push.data.RetrofitClient
import com.example.push.ui.components.AppHeader
import kotlinx.coroutines.launch

@Composable
fun ForumDetailScreen(categoryId: Int, navController: NavController) {
    var posts: List<ForumPost>? by remember { mutableStateOf(emptyList<ForumPost>()) }
    var newReplyText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(categoryId) {
        scope.launch {
            try {
                val response = RetrofitClient.api.getForumPosts(categoryId)
                if (response.status == "success") {
                    posts = response.data!!
                }
            } catch (e: Exception) {
                Log.d("ForumDetailScreen", "Помилка завантаження постів: ${e.message}")
            }
        }
    }

    AppHeader(navController, "Деталі теми") { // ⬅ Додаємо хедер
        Column(modifier = Modifier.fillMaxSize().padding(top = 86.dp)) {
            LazyColumn {
                items(posts.orEmpty()) { post ->
                    Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
                        Text(post.content, style = MaterialTheme.typography.bodyMedium)
                        Text("Автор: ${post.author}", style = MaterialTheme.typography.bodySmall, color = Color.Gray) // ⬅ Автор повідомлення
                        Text("Опубліковано: ${post.timestamp}", style = MaterialTheme.typography.bodySmall, color = Color.Gray) // ⬅ Час публікації
                    }
                }
            }

            TextField(
                value = newReplyText,
                onValueChange = { newReplyText = it },
                label = { Text("Написати відповідь") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    scope.launch {
                        try {
                            val response = RetrofitClient.api.sendForumReply(categoryId, newReplyText)
                            if (response.status == "success") {
                                posts = posts?.plus(ForumPost(posts?.size ?: 0 + 1, categoryId, newReplyText, "Користувач", "Щойно"))
                                newReplyText = ""
                            }
                        } catch (e: Exception) {
                            Log.d("ForumDetailScreen", "Помилка додавання відповіді: ${e.message}")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Надіслати")
            }
        }
    }
}