package com.example.push.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MainMenu(navController: NavController, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Push Menu", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("shop") }) { Text("Push School Shop") }
        Button(onClick = { navController.navigate("home") }) { Text("Головна") }
        Button(onClick = { navController.navigate("forum") }) { Text("Учнівський Форум") }
        Button(onClick = { navController.navigate("wishlist") }) { Text("Скарбничка побажань") }
        Button(onClick = { navController.navigate("complaints") }) { Text("Скарги") }
        Button(onClick = { navController.navigate("forms") }) { Text("Шкільна Форма") }
        Button(onClick = { navController.navigate("rewards") }) { Text("Система заохочення") }
        Button(onClick = { navController.navigate("news") }) { Text("Push News") }
        Button(onClick = { navController.navigate("market") }) { Text("Креативний Маркет") }

    }
}