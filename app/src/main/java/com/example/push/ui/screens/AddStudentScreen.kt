package com.example.push.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.push.data.RetrofitClient
import com.example.push.data.UserPreferences
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.example.push.ui.components.AppHeader

@Composable
fun AddStudentScreen(navController: NavController) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val userRole = userPrefs.getUserRole()
    var name by remember { mutableStateOf("") }
    var studentClass by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var popupMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Доступ лише для адміна
    if (userRole != "admin") {
        LaunchedEffect(Unit) {
            popupMessage = "⛔ Доступ заборонено. Тільки для адміністратора."
        }
        if (popupMessage.isNotEmpty()) {
            AlertDialog(
                onDismissRequest = { navController.popBackStack() },
                title = { Text("Помилка") },
                text = { Text(popupMessage) },
                confirmButton = {
                    Button(onClick = { navController.popBackStack() }) { Text("OK") }
                }
            )
        }
        return
    }
    AppHeader(navController, "Додати учня") {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Додати учня", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Ім'я") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = studentClass,
            onValueChange = { studentClass = it },
            label = { Text("Клас") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Телефон") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("Код") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (name.isBlank() || studentClass.isBlank() || phone.isBlank() || code.isBlank()) {
                    popupMessage = "⚠ Заповніть всі поля!"
                    return@Button
                }
                scope.launch {
                    isLoading = true
                    try {
                        val response = RetrofitClient.api.addUser(name, studentClass, phone, code)
                        if (response.status == "success") {
                            popupMessage = "✅ Учня додано!"
                            name = ""
                            studentClass = ""
                            phone = ""
                            code = ""
                        } else {
                            popupMessage = "❌ Помилка: ${response.message ?: "Невідома помилка"}"
                        }
                    } catch (e: Exception) {
                        Log.e("AddStudentScreen", "Помилка: ${e.message}")
                        popupMessage = "❌ Помилка: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Додати")
        }
        if (isLoading) {
            Spacer(modifier = Modifier.height(8.dp))
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
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
