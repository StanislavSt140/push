package com.example.push.market



import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import retrofit2.http.*

interface MarketApiService {
    @GET("push/market/getProducts.php") // ⬅ Отримання товарів (усіх або за категорією)
    suspend fun getProducts(@Query("categoryId") categoryId: Int? = null): ApiResponse<List<ProductItem>>

    @GET("push/market/getProductDetail.php") // ⬅ Отримання деталей товару
    suspend fun getProductDetail(@Query("productId") productId: Int): ApiResponse<ProductItem>

    @Multipart
    @POST("push/market/createProduct.php")
    suspend fun createProduct(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("price") price: RequestBody,
        @Part("discountPrice") discountPrice: RequestBody?,
        @Part("categoryId") categoryId: RequestBody,
        @Part image: MultipartBody.Part // ⬅ Оновлення типу `image`
    ): ApiResponse<String>

    @GET("push/market/getCategories.php") // ⬅ Отримання списку категорій
    suspend fun getCategories(): ApiResponse<List<CategoryItem>>
}
data class ApiResponse<T>(
    val status: String,
    val data: T?,
    val message: String? // Поле для повідомлень про успіх або помилку
)
data class CategoryItem(
    val id: Int,    // Унікальний ID категорії
    val name: String,  // Назва категорії
    val imageUrl: String // URL для іконки категорії
)

data class ProductItem(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val discountPrice: Double?, // ⬅ Може бути `null`, якщо знижки немає
    val imageUrl: String,
    val categoryId: Int? // ⬅ Прив’язуємо товар до категорії
)

object RetrofitClient {
    val marketApi: MarketApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://test.veloboom.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MarketApiService::class.java)
    }
}