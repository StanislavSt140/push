package com.example.push.ui.screens


import android.graphics.Color.alpha
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.push.data.NewsItem
import com.example.push.data.RetrofitClient
import com.example.push.navigation.Screen
import com.example.push.ui.components.AppHeader
import kotlinx.coroutines.launch

@Composable
fun MainScreen(navController: NavController) {
    AppHeader(navController, "Головна") {
        val newsList = remember { mutableStateOf(emptyList<NewsItem>()) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            scope.launch {
                try {
                    val response = RetrofitClient.api.getNews()
                    if (response.status == "success") {
                        newsList.value = response.news ?: emptyList()
                    } else {
                        Log.d("MainScreen", "Помилка отримання новин!")
                    }
                } catch (e: Exception) {
                    Log.d("MainScreen", "Помилка запиту до сервера: ${e.message}")
                }
            }
        }


        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(top = 86.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp) // ⬅ Додає відступи між блоками
        ) {
            item {
                ImageSlider(
                    images = listOf(
                        "https://cdn.pixabay.com/photo/2023/09/23/14/22/dahlia-8271071_640.jpg",
                        "https://img.freepik.com/free-vector/key-bunch-with-keychain-metal-ring_107791-626.jpg",
                        "https://ukrasheniya.com.ua/image/cache/catalog/sites/default/files/content/tovar/tovar-gallery/braslety-iz-natyralnogo-kamnya-ukraina-magazin-akvamarin-gorniyxryctal-agat-biruza-2-600x600.jpg",
                        "https://img.freepik.com/free-vector/key-bunch-with-keychain-metal-ring_107791-626.jpg"
                    )
                )
            }

            item { NewsSection(newsList.value, navController) }

            item { SchoolAdvantagesSection() } // ⬅ Тепер вся сторінка скролиться вниз!
        }

    }
}

@Composable
fun ImageSlider(images: List<String>) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = rememberAsyncImagePainter(images[page]),
                contentDescription = "Слайдер",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // 🔹 Навігаційні кнопки (Назад і Вперед)
        val coroutineScope = rememberCoroutineScope() // ⬅ Запам’ятовуємо корутину
        IconButton(
            onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage((pagerState.currentPage - 1).coerceAtLeast(0))
                }
            },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape) // ⬅ Напівпрозорий фон
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Назад",
                tint = Color.White
            )
        }

        IconButton(
            onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage((pagerState.currentPage + 1).coerceAtMost(images.size - 1))
                }
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape) // ⬅ Напівпрозорий фон
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Вперед",
                tint = Color.White
            )
        }
        // 🔹 Індикатори сторінок
        Row(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(images.size) { index ->
                Box(
                    modifier = Modifier
                        .size(if (pagerState.currentPage == index) 10.dp else 8.dp)
                        .background(if (pagerState.currentPage == index) Color.White else Color.Gray, CircleShape)
                        .padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
fun NewsSection(newsList: List<NewsItem>, navController: NavController) {
    val isVisible = remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background( // ✅ Використовуємо тільки градієнт, без додаткового кольору
                    Brush.linearGradient(
                        colors = listOf(Color.Magenta, Color.Cyan)
                    ),
                    shape = RoundedCornerShape(16.dp) // ⬅ Скруглені кути залишаємо
                )
                .padding(vertical = 5.dp)
        ) {
            Text(
                text = "📰 Останні новини",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 20.sp, // ⬅ Встановлюємо розмір тексту
                    fontWeight = FontWeight.Bold, // ⬅ Встановлюємо жирний шрифт
                    shadow = Shadow( // ⬅ Додаємо тінь
                        color = Color.Black.copy(alpha = 0.5f), // ⬅ Напівпрозора тінь
                        offset = Offset(4f, 4f), // ⬅ Зміщення тіні
                        blurRadius = 8f // ⬅ Розмиття тіні
                    )
                ),
                color = Color(0xFFFFFFFF),
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        AnimatedVisibility(visible = isVisible.value) {
            NewsHorizontalScroll(newsList, navController)
        }

    }
}



@Composable
fun NewsHorizontalScroll(newsList: List<NewsItem>, navController: NavController) {
    val lazyListState = rememberLazyListState() // ⬅ Оголошуємо `LazyListState`

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        state = lazyListState,
        flingBehavior = rememberSnapFlingBehavior(lazyListState)
    ) {
        itemsIndexed(newsList) { index, news ->
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .width(280.dp)
                    .clickable { navController.navigate(Screen.NewsDetail.createRoute(news.id)) },
                shape = RoundedCornerShape(16.dp), // ⬅ Закруглюємо картку
                colors = CardDefaults.cardColors(containerColor = Color.Transparent) // ⬅ Робимо фон прозорим
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(colors = listOf(Color.Magenta, Color.Cyan)), // ⬅ Градієнтний фон як у заголовку
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        if (news.imageUrl.isNotEmpty()) {
                            Image(
                                painter = rememberAsyncImagePainter(news.imageUrl),
                                contentDescription = "Новина",
                                modifier = Modifier.height(140.dp).fillMaxWidth(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier.height(140.dp).fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Зображення відсутнє", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(news.title, style = MaterialTheme.typography.titleMedium, color = Color.White)

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { navController.navigate(Screen.NewsDetail.createRoute(news.id)) },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)) // ⬅ Напівпрозора кнопка
                        ) {
                            Text("Докладніше", style = MaterialTheme.typography.labelLarge, color = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SchoolAdvantagesSection() {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color.Magenta, Color.Cyan)
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(vertical = 5.dp)
        ) {
            Text(
                text = "🎓 Переваги нашої школи",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 20.sp, // ⬅ Встановлюємо розмір тексту
                    fontWeight = FontWeight.Bold, // ⬅ Встановлюємо жирний шрифт
                    shadow = Shadow( // ⬅ Додаємо тінь
                        color = Color.Black.copy(alpha = 0.5f), // ⬅ Напівпрозора тінь
                        offset = Offset(4f, 4f), // ⬅ Зміщення тіні
                        blurRadius = 8f // ⬅ Розмиття тіні
                    )
                ),
                color = Color.White,
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val advantages = listOf(
            "✅ Початкова, середня і старша школа",
            "📊 Моніторинг навчальної діяльності",
            "👩‍🎓 До 15 учнів у класах",
            "🏫 Сучасна матеріальна база",
            "🌍 Білінгвальна освіта",
            "🌳 Просторий кампус",
            "🇬🇧 Британська та українська програми",
            "🍽 Здорове харчування",
            "🎭 Позашкільне життя",
            "📞 Спілкування з батьками"
        )

        Box(modifier = Modifier.fillMaxWidth().height(700.dp)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(advantages.size) { index ->
                    val advantage = advantages[index]

                    Box(
                        modifier = Modifier
                            .padding(12.dp)
                            .offset(y = if (index % 2 == 0) 0.dp else 20.dp)
                            .rotate(if (index % 2 == 0) -10f else 10f)
                            .background(
                                Brush.linearGradient( // ⬅ Градієнтний фон для кожного елемента
                                    colors = listOf(Color.Magenta, Color.Cyan)
                                ),
                                shape = RoundedCornerShape(50.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            text = advantage,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.White,
                                shadow = Shadow( // ⬅ Додаємо тінь
                                    color = Color.Black.copy(alpha = 0.5f), // ⬅ Напівпрозора тінь
                                    offset = Offset(4f, 4f), // ⬅ Зміщення тіні
                                    blurRadius = 8f // ⬅ Розмиття тіні
                                )
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
    FoundersSection()
}
data class Founder(val name: String, val description: String, val imageUrl: String) // ⬅ Переконайся, що поле існує
@Composable
fun FoundersSection() {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color.Magenta, Color.Cyan)
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(vertical = 5.dp)
        ) {
            Text(
                text = "👩‍🏫 Засновники школи PUSH",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 20.sp, // ⬅ Встановлюємо розмір тексту
                    fontWeight = FontWeight.Bold, // ⬅ Встановлюємо жирний шрифт
                    shadow = Shadow( // ⬅ Додаємо тінь
                        color = Color.Black.copy(alpha = 0.5f), // ⬅ Напівпрозора тінь
                        offset = Offset(4f, 4f), // ⬅ Зміщення тіні
                        blurRadius = 8f // ⬅ Розмиття тіні
                    )
                ),
                color = Color.White,
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val founders = listOf(
            Founder(
                name = "Ковальчук Олеся Валеріївна",
                description = "Люблю подорожі та навчання. Школа має бути місцем, де учень може вільно висловлювати свої думки та отримувати підтримку.",
                imageUrl = "https://push-school.com/wp-content/uploads/2020/08/img_ph_Kovalchuk.png"
            ),
            Founder(
                name = "Іванова Ганна Олександрівна",
                description = "Хочу створити школу з динамічною освітою, де є Positive learning atmosphere, Support від вчителів і можливість бути High achiever!",
                imageUrl = "https://push-school.com/wp-content/uploads/2020/08/img_ph_Ivanova.png"
            ),
            Founder(
                name = "Васенко Анна Миколаївна",
                description = "Навчання має бути зонією інтересів дітей. Ми прагнемо гармонійного розвитку та впевненості в силах для успіху!",
                imageUrl = "https://push-school.com/wp-content/uploads/2020/08/img_ph_Vasenko.png"
            )
        )

        LazyRow(modifier = Modifier.fillMaxWidth()) {
            itemsIndexed(founders) { index,founder -> // ⬅ Використовуємо `items(founders)`, а не `items(founders.size)`
                Card(
                    modifier = Modifier.padding(8.dp).width(250.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color.Magenta, Color.Cyan)
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(16.dp)
                            .height(200.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(founder.imageUrl),
                                contentDescription = "Фото ${founder.name}",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                founder.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    shadow = Shadow( // ⬅ Додаємо тінь
                                        color = Color.Black.copy(alpha = 0.5f), // ⬅ Напівпрозора тінь
                                        offset = Offset(4f, 4f), // ⬅ Зміщення тіні
                                        blurRadius = 8f // ⬅ Розмиття тіні
                                    ),
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = TextAlign.Center,
                                color = Color.White

                            )
                            Text(founder.description,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    shadow = Shadow( // ⬅ Додаємо тінь
                                        color = Color.Black.copy(alpha = 0.5f), // ⬅ Напівпрозора тінь
                                        offset = Offset(4f, 4f), // ⬅ Зміщення тіні
                                        blurRadius = 8f // ⬅ Розмиття тіні
                                    )
                                ),
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}


















