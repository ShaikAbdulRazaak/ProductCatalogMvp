package com.razzaaq.productcatalogmvp.presentation.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.razzaaq.productcatalogmvp.domain.model.Product
import com.razzaaq.productcatalogmvp.domain.model.Rating
import com.razzaaq.productcatalogmvp.domain.repository.ProductRepository
import org.junit.Rule
import org.junit.Test

private class FakeHomeProductRepository : ProductRepository {
    private val categoryProducts = listOf(
        Product(1, "Jacket", 55.99, "Jacket description", "men's clothing", "image", Rating(4.8, 20)),
        Product(2, "T-Shirt", 22.30, "T-shirt description", "men's clothing", "image", Rating(4.1, 10)),
        Product(3, "Trousers", 45.00, "Trousers description", "men's clothing", "image", Rating(3.9, 8))
    )

    override suspend fun getFeaturedProducts(): Result<List<Product>> = Result.success(categoryProducts)

    override suspend fun getProductsByCategory(category: String): Result<List<Product>> =
        Result.success(categoryProducts)

    override suspend fun searchProducts(query: String): Result<List<Product>> =
        Result.success(emptyList())

    override suspend fun getProductById(id: Int): Result<Product> =
        Result.success(categoryProducts.first())

    override suspend fun submitEnquiry(
        productId: Int,
        name: String,
        email: String,
        message: String
    ): Result<Unit> = Result.success(Unit)
}

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun selectingCategoryKeepsProductsOnHomeAndShowsCategorySections() {
        val viewModel = HomeViewModel(FakeHomeProductRepository())

        composeTestRule.setContent {
            HomeScreen(
                onProductClick = {},
                onSearchClick = {},
                viewModel = viewModel
            )
        }

        composeTestRule.onNodeWithText("Men’s clothing").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Top rated in this category").assertIsDisplayed()
        composeTestRule.onNodeWithText("More from this category").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jacket").assertIsDisplayed()
    }
}
