package com.example.push.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @GET("push/forumCategories.php") // ⬅ Отримуємо список категорій
    suspend fun getForumCategories(): ApiResponse<List<ForumCategory>>

    @GET("push/forumPosts.php") // ⬅ Отримуємо всі пости у категорії
    suspend fun getForumPosts(@Query("categoryId") categoryId: Int): ApiResponse<List<ForumPost>>

    @POST("push/createForumCategory.php") // ⬅ Створюємо нову категорію
    @FormUrlEncoded
    suspend fun createForumCategory(@Field("title") title: String): ResponseStatus

    @POST("push/sendForumReply.php") // ⬅ Додаємо новий пост у категорію
    @FormUrlEncoded
    suspend fun sendForumReply(
        @Field("categoryId") categoryId: Int,
        @Field("message") message: String
    ): ResponseStatus

    @GET("push/index.php")
    suspend fun verifyCode(@Query("code") code: String): AuthResponse

    @GET("push/news.php")
    suspend fun getNews(): NewsResponse

    @GET("push/scoolForm.php")
    suspend fun getSchoolForm(): SchoolFormResponse

    @POST("push/sendSuggestion.php") // ⬅ Відправка даних на сервер
    @FormUrlEncoded
    suspend fun sendSuggestion(
        @Field("fullName") fullName: String,
        @Field("className") className: String,
        @Field("message") message: String
    ): ResponseStatus

    @GET("push/shop.php")
    suspend fun getProducts(): ProductResponse

    @GET("push/ddProduct.php")
    suspend fun addProduct(): ProductResponse

    @GET("push/getComplaints.php") // ⬅ Отримуємо список скарг
    suspend fun getComplaints(): ApiResponse<List<ComplaintItem>>

    @POST("push/sendComplaint.php") // ⬅ Надсилаємо скаргу
    @FormUrlEncoded
    suspend fun sendComplaint(
        @Field("userName") userName: String,
        @Field("complaintText") complaintText: String
    ): ResponseStatus
    @GET("push/getComplaintDetails.php") // ⬅ Отримуємо деталі скарги
    suspend fun getComplaintDetails(@Query("complaintId") complaintId: Int): ApiResponse<ComplaintItem>

    @GET("push/getRewards.php") // ⬅ Отримуємо список нагород
    suspend fun getRewards(): ApiResponse<List<RewardItem>>

    @GET("push/getRewardDetails.php") // ⬅ Отримуємо деталі нагороди
    suspend fun getRewardDetails(@Query("rewardId") rewardId: Int): ApiResponse<RewardItem>


    @GET("push/getWishlistCategories.php") // ⬅ Отримуємо список категорій побажань
    suspend fun getWishlistCategories(): ApiResponse<List<WishlistCategory>>

    @GET("push/getWishlist.php") // ⬅ Отримуємо побажання у вибраній категорії
    suspend fun getWishlist(@Query("categoryId") categoryId: Int): ApiResponse<List<WishlistItem>>

    @POST("push/sendWishlistItem.php") // ⬅ Додаємо побажання у вибрану категорію
    @FormUrlEncoded
    suspend fun sendWishlistItem(
        @Field("categoryId") categoryId: Int,
        @Field("content") content: String
    ): ResponseStatus

    @GET("push/getWishlistItem.php") // ⬅ Отримуємо деталі конкретного побажання
    suspend fun getWishlistItem(@Query("wishId") wishId: Int): ApiResponse<WishlistItem>
}

data class ProductResponse(val status: String, val products: List<ProductItem>)
data class ResponseStatus(val status: String, val message: String) // ⬅ Отримуємо статус та повідомлення
data class SchoolFormResponse(val status: String, val form: SchoolFormItem)
data class SchoolFormItem(val title: String, val content: String, val imageUrl: String) // ⬅ Контент у HTML
data class AuthResponse(val status: String, val message: String)
data class NewsResponse(val status: String, val news: List<NewsItem>)
data class NewsItem(val id: Int, val title: String, val content: String, val imageUrl: String) // ⬅ Додаємо `imageUrl`

data class ProductItem(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val price: Double,
    val discountPrice: Double?,
    val rating: Float,
    val description: String // ⬅ Повний опис товару (показується в деталях)
)
data class ApiResponse<T>(
    val status: String,
    val data: T?
)
data class WishlistCategory(
    val id: Int, // ⬅ ID категорії (наприклад, "class", "school", "canteen")
    val imageUrl: String,
    val name: String // ⬅ Назва категорії
)
data class ForumCategory(
    val id: Int,
    val title: String,
    val author: String,
    val description: String
)
data class ForumPost(
    val id: Int,
    val categoryId: Int,
    val content: String,
    val author: String,
    val timestamp: String
)
data class ComplaintItem(
    val id: Int,
    val author: String,
    val content: String,
    val timestamp: String
)
data class RewardItem(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val description: String
)

data class WishlistItem(
    val id: Int,
    val content: String,
    val timestamp: String
)
object RetrofitClient {
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://test.veloboom.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}