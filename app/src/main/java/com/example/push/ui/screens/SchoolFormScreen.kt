package com.example.push.ui.screens

import android.text.Html
import android.widget.TextView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.push.data.RetrofitClient
import com.example.push.data.SchoolFormItem
import com.example.push.ui.components.AppHeader
import kotlinx.coroutines.launch

@Composable
fun SchoolFormScreen(navController: NavController) {
    var formItem by remember { mutableStateOf<SchoolFormItem?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    val gradientColors = listOf(
        Color(0xFFFF4081),  // Pink
        Color(0xFF1E0F4F)   // Dark Purple
    )
    LaunchedEffect(Unit) {
        scope.launch {
            val response = RetrofitClient.api.getSchoolForm()
            if (response.status == "success") {
                formItem = response.form
            }
        }
    }

    AppHeader(navController, formItem?.title ?: "Шкільна Форма") {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = gradientColors,
                        start = androidx.compose.ui.geometry.Offset(
                            Float.POSITIVE_INFINITY,
                            Float.POSITIVE_INFINITY
                        ), // bottom-right
                        end = androidx.compose.ui.geometry.Offset(0f, 0f) // top-left
                    )
                )
        ) {

        Column(modifier = Modifier.fillMaxSize().padding(top = 104.dp, start = 16.dp, end = 16.dp)) {
            formItem?.let { form ->
                Image(
                    painter = rememberAsyncImagePainter(form.imageUrl),
                    contentDescription = null,
                    modifier = Modifier.height(250.dp).fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                AndroidView(factory = { context ->
                    TextView(context).apply {
                        text = Html.fromHtml(form.content, Html.FROM_HTML_MODE_COMPACT)
                        setTextColor(Color.White.toArgb())
                    }
                })
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF4081)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(48.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    Text("Запропонувати побажання")
                }
            } ?: CircularProgressIndicator(modifier = Modifier.padding(16.dp))

            if (showSnackbar) {
                Snackbar(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), // ⬅ Відступ і ширина
                    containerColor = Color(0xFF4CAF50),
                    action = {
                        TextButton(onClick = { showSnackbar = false }) {
                            Text("OK", color = Color.White)
                        }
                    }
                ) {
                    Text(snackbarMessage, color = Color.White)
                }
            }
        }
        }
    }

    if (showDialog) {
        SuggestionDialog(
            onDismiss = { showDialog = false },
            onSuccess = { message ->
                snackbarMessage = message
                showSnackbar = true
            }
        )
    }
}

@Composable
fun SuggestionDialog(onDismiss: () -> Unit, onSuccess: (String) -> Unit) {
    var fullName by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isSending by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { if (!isSending) onDismiss() },
        confirmButton = {
            Button(onClick = {
                if (fullName.isBlank() || className.isBlank() || message.isBlank()) {
                    isError = true
                } else {
                    isSending = true
                    scope.launch {
                        try {
                            val response = RetrofitClient.api.sendSuggestion(fullName, className, message)
                            if (response.status == "success") {
                                onSuccess("✅ Ваше побажання успішно надіслано!")
                                onDismiss()
                            } else {
                                onSuccess("❌ Помилка під час надсилання.")
                            }
                        } catch (e: Exception) {
                            onSuccess("⚠ Немає зв’язку із сервером.")
                        }
                        isSending = false
                    }
                }
            }) {
                Text(if (isSending) "Відправка..." else "Надіслати")
            }
        },
        dismissButton = {
            TextButton(onClick = { if (!isSending) onDismiss() }) {
                Text("Скасувати")
            }
        },
        title = { Text("Запропонувати побажання") },
        text = {
            Column {
                CustomTextField("ПІБ", fullName, { fullName = it }, isError && fullName.isBlank())
                Spacer(modifier = Modifier.height(8.dp))
                CustomTextField("Клас", className, { className = it }, isError && className.isBlank())
                Spacer(modifier = Modifier.height(8.dp))
                CustomTextField("Побажання", message, { message = it }, isError && message.isBlank())
            }
        }
    )
}

@Composable
fun CustomTextField(label: String, value: String, onValueChange: (String) -> Unit, isError: Boolean) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().then(
            if (isError) Modifier.border(2.dp, Color.Red) else Modifier
        ),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            errorContainerColor = Color.Red
        )
    )
}