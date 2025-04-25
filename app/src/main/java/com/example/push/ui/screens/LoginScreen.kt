package com.example.push.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.push.data.RetrofitClient
import com.example.push.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    var code by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Введіть код доступу", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("Код") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            scope.launch {
                val response = RetrofitClient.api.verifyCode(code)
                message = response.message
                if (response.status == "success") {
                    navController.navigate(Screen.Home.route) // ⬅ Перехід до головного меню!
                }
            }
        }) {
            Text("Увійти")
        }

        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = message)
        }
    }
}