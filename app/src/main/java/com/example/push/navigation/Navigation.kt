package com.example.push.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.push.market.CategoryDetailScreen
import com.example.push.market.CreateProductScreen
import com.example.push.market.MarketScreen
import com.example.push.market.ProductDetailScreen
import com.example.push.market.RetrofitClient
import com.example.push.navigation.Screen.Market
import com.example.push.ui.screens.*

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Menu : Screen("menu")
    object Home : Screen("home")
    object Shop : Screen("shop")
    object Forum : Screen("forum")
    object Forms : Screen("forms")
    object Wishlist : Screen("wishlist")
    object Complaints : Screen("complaints")
    object Rewards : Screen("rewards")
    object News : Screen("news")

    object Market : Screen("market")
    object ProductDetail : Screen("productDetail/{productId}") {
        fun createRoute(productId: Int) = "productDetail/$productId"
    }
    object CreateProduct : Screen("createProduct")

    object NewsDetail : Screen("newsDetail/{newsId}") {
        fun createRoute(newsId: Int) = "newsDetail/$newsId"
    }
    object ShopDetail : Screen("shopDetail/{productId}") {
        fun createRoute(productId: Int) = "shopDetail/$productId"
    }
    object ForumDetail : Screen("forumDetail/{categoryId}") { // ⬅ Додаємо перехід до конкретної теми форуму
        fun createRoute(categoryId: Int) = "forumDetail/$categoryId"
    }
    object ComplaintsDetail : Screen("complaintsDetail/{complaintId}") {
        fun createRoute(complaintId: Int) = "complaintsDetail/$complaintId"
    }
    object RewardsDetail : Screen("rewardsDetail/{rewardId}") {
        fun createRoute(rewardId: Int) = "rewardsDetail/$rewardId"
    }
    object WishlistCategory : Screen("wishlistCategory/{categoryId}") {
        fun createRoute(categoryId: Int) = "wishlistCategory/$categoryId"
    }
    object CategoryDetail : Screen("categoryDetail/{categoryId}") {
        fun createRoute(categoryId: Int) = "categoryDetail/$categoryId"
    }
}

@Composable
fun PushNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Market.route) {
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Home.route) { MainScreen(navController) }
        composable(Screen.Menu.route) { MainMenu(navController) }
        composable(Screen.Forum.route) { ForumScreen(navController) }
        composable(Screen.Shop.route) { ShopScreen(navController) }
        composable(Screen.News.route) { NewsScreen(navController) }
        composable(Screen.Forms.route) { SchoolFormScreen(navController) }
        composable(Screen.Complaints.route) { ComplaintsScreen(navController) }
        composable(Screen.Rewards.route) { RewardsScreen(navController) }
        composable(Screen.Wishlist.route) { WishlistScreen(navController) }

        composable(Screen.WishlistCategory.route) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")?.toIntOrNull()
            if (categoryId != null) {
                WishlistCategoryScreen(categoryId, navController)
            }
        }
        composable(Screen.RewardsDetail.route) { backStackEntry ->
            val rewardId = backStackEntry.arguments?.getString("rewardId")?.toIntOrNull()
            if (rewardId != null) {
                RewardsDetailScreen(rewardId, navController)
            }
        }
        composable(Screen.NewsDetail.route) { backStackEntry ->
            val newsId = backStackEntry.arguments?.getString("newsId")?.toIntOrNull()
            if (newsId != null) {
                NewsDetailScreen(newsId, navController)
            }
        }
        composable(Screen.ShopDetail.route) { backStackEntry -> // ⬅ Додаємо перехід на деталі товару
            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
            if (productId != null) {
                ShopDetailScreen(productId, navController)
            }
        }
        composable(Screen.ForumDetail.route) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")?.toIntOrNull()
            if (categoryId != null) {
                ForumDetailScreen(categoryId, navController)
            }
        }
        composable(Screen.ComplaintsDetail.route) { backStackEntry ->
            val complaintId = backStackEntry.arguments?.getString("complaintId")?.toIntOrNull()
            if (complaintId != null) {
                ComplaintsDetailScreen(complaintId, navController)
            }
        }
        composable(Screen.Market.route) { MarketScreen(navController) }
        composable(Screen.CategoryDetail.route) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")?.toIntOrNull()
            if (categoryId != null) {
                CategoryDetailScreen(categoryId, navController)
            }
        }

        composable(Screen.CreateProduct.route) {
            CreateProductScreen(navController, RetrofitClient.marketApi, rememberCoroutineScope())
        }
        composable(Screen.ProductDetail.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
            if (productId != null) {
                ProductDetailScreen(productId, navController)
            }
        }
    }
}



