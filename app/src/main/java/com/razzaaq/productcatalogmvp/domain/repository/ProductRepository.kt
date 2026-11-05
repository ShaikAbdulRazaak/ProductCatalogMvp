package com.razzaaq.productcatalogmvp.domain.repository

import com.razzaaq.productcatalogmvp.domain.model.Product

interface ProductRepository {
    suspend fun getFeaturedProducts(): Result<List<Product>>
    suspend fun getProductsByCategory(category: String): Result<List<Product>>
    suspend fun searchProducts(query: String): Result<List<Product>>
    suspend fun getProductById(id: Int): Result<Product>
    suspend fun submitEnquiry(
        productId: Int,
        name: String,
        email: String,
        message: String
    ): Result<Unit>
}