package com.example.push.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.push.ui.components.AppHeader

@SuppressLint("SuspiciousIndentation")
@Composable
fun MainMenu(navController: NavController, modifier: Modifier = Modifier) {
    val gradientColors = listOf(
        Color(0xFFFF4081),  // Pink
        Color(0xFF1E0F4F)   // Dark Purple
    )


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
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(top = 56.dp, start = 16.dp, end = 16.dp, bottom = 70.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                // Greeting Section
                Text(
                    text = "Привіт, друже!",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = "Ти на шляху до 100%!",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Кольори для карток
                val cardColors = listOf(
                    Color(0xFFF57C00),
                    Color(0xFF3F51B5),
                    Color(0xFF9C27B0),
                    Color(0xFFFFB300),
                    Color(0xFFFFB300),
                    Color(0xFF3F51B5),
                    Color(0xFF3F51B5),
                    Color(0xFFF57C00),
                    Color(0xFFFFB300)
                )

                // 🔹 Список елементів меню з іконками
                val menuItems = listOf(
                    Triple("Push School Shop", "shop", Icons.Default.ShoppingCart),
                    Triple("Головна", "home", Icons.Default.Home),
                    Triple("Учнівський Форум", "forum", Icons.Default.Face),
                    Triple("Скарбничка побажань", "wishlist", Icons.Default.Favorite),
                    Triple("Скарги", "complaints", Icons.Default.Warning),
                    Triple("Шкільна Форма", "forms", Icons.Default.Check),
                    Triple("Система заохочення", "rewards", Icons.Default.ThumbUp),
                    Triple("Push News", "news", Icons.Default.Info),
                    Triple("Креативний Маркет", "market", Icons.Default.ShoppingCart)
                )

// 🔹 Відображаємо плитки по 2 в ряд
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(menuItems) { index, (title, route, icon) ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.2f)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(12.dp),
                                    spotColor = Color.Black.copy(alpha = 0.4f)
                                )
                                .clip(RoundedCornerShape(12.dp))
                                .background(cardColors[index])
                                .clickable { navController.navigate(route) }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = title,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .shadow(
                                            elevation = 4.dp,
                                            shape = RoundedCornerShape(6.dp),
                                            spotColor = Color.Black.copy(alpha = 0.3f)
                                        )
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White.copy(alpha = 0.4f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = title,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

