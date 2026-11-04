package com.razzaaq.productcatalogmvp.data.remote

import com.razzaaq.productcatalogmvp.domain.model.Product
import com.razzaaq.productcatalogmvp.domain.model.Rating
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val rating: RatingDto
) {
    fun toDomain() =
        Product(id, title, price, description, category, image, Rating(rating.rate, rating.count))
}

@Serializable
data class RatingDto(
    val rate: Double,
    val count: Int
)

@Serializable
data class EnquiryRequest(
    val productId: Int,
    val name: String,
    val email: String,
    val message: String
)