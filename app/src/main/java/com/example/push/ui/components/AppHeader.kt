package com.example.push.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.push.navigation.Screen
import com.example.push.R
import com.example.push.data.UserPreferences
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeader(navController: NavController, screenTitle: String, content: @Composable () -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var isMenuVisible by remember { mutableStateOf(false) }

    // ⬇ Отримуємо ім'я та роль користувача
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val userName = userPrefs.getUserName()
    val userRole = userPrefs.getUserRole()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(250.dp).background(Color.White)) {
                Column(
                    modifier = Modifier.fillMaxHeight().padding(24.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.logo_not_fon),
                        contentDescription = "Логотип",
                        modifier = Modifier.size(120.dp).padding(bottom = 16.dp)
                    )
                    Text("Меню навігації", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))

                    // ⬇ Відображення даних користувача
                    Text(text = "👤 $userName", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "🎭 Роль: $userRole", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    // ⬇ СКРОЛЬОВАНИЙ список кнопок меню
                    Column(
                        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CustomNavButton("Головна") { navController.navigate(Screen.Home.route) }
                        CustomNavButton("Головне меню") { navController.navigate(Screen.Menu.route) }
//                        if (userRole == "admin") {
//                            CustomNavButton("Додати учня") { navController.navigate(Screen.AddStudent.route) }
//
//                        }
                        CustomNavButton("Огляд учнів") { navController.navigate(Screen.ClassList.route) }
                        CustomNavButton("Push School Shop") { navController.navigate(Screen.Shop.route) }
                        CustomNavButton("Учнівський Форум") { navController.navigate(Screen.Forum.route) }
                        CustomNavButton("Скарбничка побажань") { navController.navigate(Screen.Wishlist.route) }
                        CustomNavButton("Скарги") { navController.navigate(Screen.Complaints.route) }
                        CustomNavButton("Шкільна форма") { navController.navigate(Screen.Forms.route) }
                        CustomNavButton("Система заохочення") { navController.navigate(Screen.Rewards.route) }
                        CustomNavButton("Push News") { navController.navigate(Screen.News.route) }
                        CustomNavButton("Креативний Маркет") { navController.navigate(Screen.Market.route) }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(screenTitle, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                            isMenuVisible = true
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Меню", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF03736A))
                )
            },
            containerColor = Color.White
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                content()
            }
        }
    }
}

// 📌 **Оформлення кнопок**
@Composable
fun CustomNavButton(text: String, onClick: () -> Unit) {
    Column {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(0.dp), // ⬅ Без скруглень
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent) // ⬅ Прозорий фон
        ) {
            Box(modifier = Modifier.fillMaxWidth()) { // ⬅ Використовуємо Box для вирівнювання
                Text(
                    text,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 16.dp) // ⬅ Тепер текст вирівняний
                )
            }
        }
        Divider(color = Color.LightGray, thickness = 1.dp) // ⬅ Лінія розмежування
    }
}