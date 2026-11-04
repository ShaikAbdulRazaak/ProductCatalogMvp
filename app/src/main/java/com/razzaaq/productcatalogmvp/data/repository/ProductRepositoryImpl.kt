package com.razzaaq.productcatalogmvp.data.repository

import com.razzaaq.productcatalogmvp.data.remote.EnquiryRequest
import com.razzaaq.productcatalogmvp.data.remote.StoreApi
import com.razzaaq.productcatalogmvp.domain.model.Product
import com.razzaaq.productcatalogmvp.domain.repository.ProductRepository
import jakarta.inject.Inject
import retrofit2.HttpException
import java.io.IOException

class ProductRepositoryImpl @Inject constructor(
    private val api: StoreApi
) : ProductRepository {

    override suspend fun getFeaturedProducts(): Result<List<Product>> = safeApiCall {
        api.getFeaturedProducts().map { it.toDomain() }
    }

    override suspend fun getProductsByCategory(category: String): Result<List<Product>> = safeApiCall {
        api.getProductsByCategory(category).map { it.toDomain() }
    }

    override suspend fun searchProducts(query: String): Result<List<Product>> = safeApiCall {
        api.searchProducts(query).map { it.toDomain() }
    }

    override suspend fun getProductById(id: Int): Result<Product> = safeApiCall {
        api.getProductById(id).toDomain()
    }

    override suspend fun submitEnquiry(productId: Int, name: String, email: String, message: String): Result<Unit> = safeApiCall {
        api.submitEnquiry(EnquiryRequest(productId, name, email, message))
    }

    // Centralized error handler
    private inline fun <T> safeApiCall(apiCall: () -> T): Result<T> {
        return try {
            Result.success(apiCall())
        } catch (e: IOException) {
            Result.failure(Exception("Network error. Please check your connection.", e))
        } catch (e: HttpException) {
            Result.failure(Exception("Server error (${e.code()}). Please try again later.", e))
        } catch (e: Exception) {
            Result.failure(Exception("An unexpected error occurred.", e))
        }
    }
}