package com.example.push.ui.screens


import android.graphics.Color.alpha
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.push.data.NewsItem
import com.example.push.data.RetrofitClient
import com.example.push.navigation.Screen
import com.example.push.ui.components.AppHeader
import kotlinx.coroutines.launch

// Виправлені імпорти для кастомного Shape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.zIndex

@Composable
fun MainScreen(navController: NavController) {
    val gradientColors = listOf(
        Color(0xFFFF4081),  // Pink
        Color(0xFF1E0F4F)   // Dark Purple
    )

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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(top = 98.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp) // 
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

            item { SchoolAdvantagesSection() } // 
        }
        }

    }
}

@Composable
fun ImageSlider(images: List<String>) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = rememberAsyncImagePainter(images[page]),
                contentDescription = "",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // 
        val coroutineScope = rememberCoroutineScope() // 
        IconButton(
            onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage((pagerState.currentPage - 1).coerceAtLeast(0))
                }
            },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape) // 
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "",
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
                .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape) // 
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "",
                tint = Color.White
            )
        }
        // 
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            // 
            Box(
                modifier = Modifier
                    .shadow(8.dp, shape = RoundedCornerShape(12.dp))
                    .background(Color(0xFFFF5C8D), shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text("Події школи", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(8.dp))
            //
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .weight(1f)
                    .background(Color(0xFF005B9F), shape = RoundedCornerShape(3.dp))
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
        LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(newsList) { index, news ->
            NewsCard(news)
        }
    }
}
@Composable
fun NewsCard(news: NewsItem) {
    Column(
        modifier = Modifier
            .width(350.dp)
            .shadow(8.dp, shape = RoundedCornerShape(16.dp))
            .background(Color(0xFF00BCD4), shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        //
        if (!news.imageUrl.isNullOrEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(news.imageUrl),
                contentDescription = news.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(12.dp))
        }
        Text(news.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF005B9F))
        Spacer(Modifier.height(8.dp))
        Text(news.content, color = Color.White, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Докладніше",
                color = Color.White,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .shadow(8.dp, shape = RoundedCornerShape(12.dp))
                    .background(Color(0xFFFF5C8D), shape = RoundedCornerShape(12.dp)) //
                    .padding(horizontal = 12.dp, vertical = 6.dp) //
            )
           // Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFFF5C8D))
        }
    }
}
@Composable
fun SchoolAdvantagesSection() {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            //
            Box(
                modifier = Modifier
                    .shadow(8.dp, shape = RoundedCornerShape(12.dp))
                    .background(Color(0xFFFF5C8D), shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text("Переваги нашої школи", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(8.dp))
            //
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .weight(1f)
                    .background(Color(0xFF005B9F), shape = RoundedCornerShape(3.dp))
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val advantages = listOf(
            "Початкова, середня і старша школа в одній будівлі",
            "Моніторинг динаміки навчальної діяльності",
            "Кількість дітей у класах - до 15 осіб",
            "Сучасна матеріально-технічна база",
            "Білінгвальна освіта",
            "Багато простору для прогулянок та навчання",
            "Британська і українська програми навчання",
            "Смачне харчування",
            "Цікаве позашкільне життя",
            "Зворотній зв'язок із батьками",
        )
        val colors = listOf(Color(0xFF005B9F), Color(0xFF00BCD4)) // ,

        Column(Modifier.padding(16.dp)) {
            //
            // (
            Spacer(Modifier.height(16.dp))
            advantages.forEachIndexed { i, text ->
                AdvantageItem(
                    text = text,
                    number = i + 1,
                    backgroundColor = colors[i % colors.size],
                    isRight = i % 2 == 1,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    }
    FoundersSection()
}


class ArrowShape(private val isRight: Boolean) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            if (isRight) {
                moveTo(0f, 0f)
                lineTo(size.width * 0.85f, 0f)
                lineTo(size.width, size.height / 2)
                lineTo(size.width * 0.85f, size.height)
                lineTo(0f, size.height)
                lineTo(size.width * 0.15f, size.height / 2)
                close()
            } else {
                moveTo(size.width, 0f)
                lineTo(size.width * 0.15f, 0f)
                lineTo(0f, size.height / 2)
                lineTo(size.width * 0.15f, size.height)
                lineTo(size.width, size.height)
                lineTo(size.width * 0.85f, size.height / 2)
                close()
            }
        }
        return Outline.Generic(path)
    }
}

@Composable
fun AdvantageItem(
    text: String,
    number: Int,
    backgroundColor: Color,
    isRight: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isRight) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .width(240.dp)
                .height(56.dp)
                .clip(ArrowShape(isRight))
                .background(backgroundColor)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (isRight) Arrangement.End else Arrangement.Start
            ) {
                if (!isRight) {
                    Text(
                        text = text,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 12.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                            .wrapContentHeight(Alignment.CenterVertically)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .offset(x = -10.dp, y = -2.dp) // ✅ Рухаємо кружок трохи вліво і вгору
                            .zIndex(5f) // ✅ Виносимо на передній план
                            .background(Color.White, shape = CircleShape)
                            .size(36.dp), // ✅ Збільшуємо розмір
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = number.toString(),
                            color = backgroundColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .offset(x = 10.dp, y = -2.dp) // ✅ Рухаємо кружок трохи вправо і вгору
                            .zIndex(5f)
                            .background(Color.White, shape = CircleShape)
                            .size(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = number.toString(),
                            color = backgroundColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = text,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        lineHeight = 12.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp)
                            .wrapContentHeight(Alignment.CenterVertically)
                            .wrapContentWidth(Alignment.End)
                    )
                }
            }
        }
    }
}

@Composable
fun FoundersSection() {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            //
            Box(
                modifier = Modifier
                    .shadow(8.dp, shape = RoundedCornerShape(12.dp))
                    .background(Color(0xFFFF5C8D), shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text("Засновниці PUSH school", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(8.dp))
            //
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .weight(1f)
                    .background(Color(0xFF005B9F), shape = RoundedCornerShape(3.dp))
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val founders = listOf(
            Founder(
                name = "Ковальчук Олеся Валеріївна",
                description = "Більш за все я люблю подорожувати та навчатися. Ще школяркою я свого часу потрапила до Голландії за програмою обміну учнями. Тоді була дуже вражена вільним діалогом, що відбувався між учнями й учителями, та дружелюбною атмосферою у школі, яку там відвідала. З того часу школа в моїх думках була саме такою. Зараз я вже сама маю двох славних діточок, і, як мати, я хочу, щоб вони навчалися саме в такій школі, де дитина вільно висловлює та аргументує свою точку зору, де вчитель – це друг, ментор та наставник, де є дружба та взаємоповага. Саме таку школу ми створюємо!",
                imageUrl = "https://push-school.com/wp-content/uploads/2020/08/img_ph_Kovalchuk.png"
            ),
            Founder(
                name = "Іванова Ганна Олександрівна",
                description = "Чому батьки відкривають власні школи? Насамперед це прагнення створити для своєї дитини умови здобуття грунтовної освіти та всебічного розвитку. Тому в нашій школі ми обрали рух до більш динамічної та осучасненої освіти. Отримання якісної освіти можливе тільки за умов перебування в школі з Positive learning atmosphere, де можна залишатися Unique, де достатньо Support з боку вчителів. Тільки завдяки цим умовам можливо стати High achiever в майбутньому!",
                imageUrl = "https://push-school.com/wp-content/uploads/2020/08/img_ph_Ivanova.png"
            ),
            Founder(
                name = "Васенко Анна Миколаївна",
                description = "Ідея відкрити приватну школу виникла завдяки власному досвіду. Як матір чотирьох чудових дітей, та я впевнена, що навчання має бути для дітей зоною їх інтересів та особистої відповідальності. Головним завданням школи є підтримка дитини як особистості та її прагнень до знань, а також надання впевненості в своїх силах. Тому, ми прагнемо гармонійного розвитку дітей в комфортних умовах, формування успішності сьогодні і благополуччя в майбутньому. Для цього ми й створюємо нашу школу. Приєднуйтесь до нас!",
                imageUrl = "https://push-school.com/wp-content/uploads/2020/08/img_ph_Vasenko.png"
            )
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            itemsIndexed(founders) { index, founder ->
                FounderCard(founder)
            }
        }
    }
}

@Composable
fun FounderCard(founder: Founder) {
    Column(
        modifier = Modifier
            .width(340.dp)
            .shadow(10.dp, shape = RoundedCornerShape(20.dp))
            .background(Color(0xFF005B9F), shape = RoundedCornerShape(20.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(founder.imageUrl),
            contentDescription = "",
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(16.dp))
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(16.dp))
        Text(
            founder.name,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            style = LocalTextStyle.current.copy(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.5f),
                    offset = Offset(2f, 2f),
                    blurRadius = 8f
                )
            )
        )
        Spacer(Modifier.height(16.dp))
        Text(
            founder.description,
            color = Color.White,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
    }
}

data class Founder(val name: String, val description: String, val imageUrl: String) // 
