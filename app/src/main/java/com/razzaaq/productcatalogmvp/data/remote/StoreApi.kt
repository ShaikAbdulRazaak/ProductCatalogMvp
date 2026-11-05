package com.razzaaq.productcatalogmvp.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StoreApi {
    @GET("products/featured")
    suspend fun getFeaturedProducts(): List<ProductDto>

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(@Path("category") category: String): List<ProductDto>

    @GET("products/search")
    suspend fun searchProducts(@Query("q") query: String): List<ProductDto>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): ProductDto

    @POST("enquiries")
    suspend fun submitEnquiry(@Body request: EnquiryRequest)
}