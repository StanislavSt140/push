package com.example.push.market



import com.example.push.data.UserItem
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import retrofit2.http.*

interface MarketApiService {
    @GET("push/market/getProducts.php")
    suspend fun getProducts(@Query("categoryId") categoryId: Int? = null): ApiResponse<List<ProductItem>>

    @GET("push/market/getProductDetail.php")
    suspend fun getProductDetail(@Query("productId") productId: Int): ApiResponse<ProductItem>

    @GET("push/market/deleteProduct.php")
    suspend fun deleteProduct(@Query("productId") productId: Int): ApiResponse<ProductItem>


    @Multipart
    @POST("push/market/createProduct.php")
    suspend fun createProduct(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("price") price: RequestBody,
        @Part("discountPrice") discountPrice: RequestBody?,
        @Part("categoryId") categoryId: RequestBody,
        @Part image: MultipartBody.Part,
        @Part("userId") userId: RequestBody,
    ): ApiResponse<String>

    @GET("push/market/getCategories.php")
    suspend fun getCategories(): ApiResponse<List<CategoryItem>>

    @Multipart
    @POST("push/market/updateProduct.php")
    suspend fun updateProduct(
        @Part("productId") productId: RequestBody,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("price") price: RequestBody,
        @Part("rating") rating: RequestBody,
        @Part("discountPrice") discountPrice: RequestBody?,
        @Part("categoryId") categoryId: RequestBody,
        @Part image: MultipartBody.Part?
    ): ApiResponse<String>
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
    val rating: Int,
    val title: String,
    val description: String,
    val price: Double,
    val discountPrice: Double?, // ⬅ Може бути `null`, якщо знижки немає
    val imageUrl: String,
    val categoryId: Int?, // ⬅ Прив’язуємо товар до категорії
    val user: UserItem?
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