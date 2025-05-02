package com.example.push.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.push.data.RetrofitClient
import com.example.push.data.WishlistItem
import com.example.push.navigation.Screen.WishlistCategory
import com.example.push.ui.components.AppHeader
import kotlinx.coroutines.launch

@Composable
fun WishlistCategoryScreen(categoryId: Int, navController: NavController) {
    val wishlist = remember { mutableStateOf(emptyList<WishlistItem>()) }
    val scope = rememberCoroutineScope()
    var isDialogOpen by remember { mutableStateOf(false) }
    var newWish by remember { mutableStateOf("") }

    val categoryTitle = remember { mutableStateOf("Категорія") }

    LaunchedEffect(categoryId) {
        scope.launch {
            try {
                val responseCategories = RetrofitClient.api.getWishlistCategories()
                if (responseCategories.status == "success") {
                    val categoryIdInt = categoryId
                    if (categoryIdInt != null) {
                        val categoryTitle = responseCategories.data!!.find { it.id == categoryIdInt }?.name ?: "Категорія"
                    }
                    Log.d("WishlistCategory", "Категорія: ${categoryTitle.value}")
                }

                val responseWishlist = RetrofitClient.api.getWishlist(categoryId)
                if (responseWishlist.status == "success") {
                    wishlist.value = responseWishlist.data!!
                    wishlist.value.forEach { Log.d("WishlistItem", "Побажання: ${it.content}") }
                }
            } catch (e: Exception) {
                Log.d("WishlistCategoryScreen", "Помилка завантаження: ${e.message}")
            }
        }
    }

    AppHeader(navController, categoryTitle.value) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { isDialogOpen = true },
                    containerColor = Color.Blue,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Додати побажання")
                }
            }
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(top = 76.dp)) {

                LazyColumn {
                    itemsIndexed(wishlist.value) { index, categoryId ->
                        WishlistItemView(categoryId) {
                            navController.navigate(WishlistCategory.createRoute(categoryId.id))
                        }
                    }
                }
            }

            if (isDialogOpen) {
                AlertDialog(
                    onDismissRequest = { isDialogOpen = false },
                    title = { Text("Нове побажання") },
                    text = {
                        Column {
                            TextField(
                                value = newWish,
                                onValueChange = { newWish = it },
                                label = { Text("Введіть ваше побажання") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                scope.launch {
                                    try {
                                        val response = RetrofitClient.api.sendWishlistItem(categoryId, newWish)
                                        if (response.status == "success") {
                                            wishlist.value = wishlist.value + WishlistItem(wishlist.value.size + 1, newWish, "Щойно")
                                            newWish = ""
                                            isDialogOpen = false


                                        }
                                    } catch (e: Exception) {
                                        Log.d("WishlistCategoryScreen", "Помилка додавання: ${e.message}")
                                    }
                                }
                            }
                        ) {
                            Text("Додати")
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
fun WishlistItemView(wish: WishlistItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(wish.content, style = MaterialTheme.typography.bodyMedium)
            Text("Дата: ${wish.timestamp}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}