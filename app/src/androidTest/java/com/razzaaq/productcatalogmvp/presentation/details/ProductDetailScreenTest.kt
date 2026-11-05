package com.razzaaq.productcatalogmvp.presentation.details

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.SavedStateHandle
import com.razzaaq.productcatalogmvp.domain.model.Product
import com.razzaaq.productcatalogmvp.domain.model.Rating
import com.razzaaq.productcatalogmvp.domain.repository.ProductRepository
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FakeProductRepositoryDetails : ProductRepository {
    var productResult: Result<Product> = Result.failure(Exception())

    override suspend fun getFeaturedProducts(): Result<List<Product>> = Result.success(emptyList())
    override suspend fun getProductsByCategory(category: String): Result<List<Product>> = Result.success(emptyList())
    override suspend fun getProductById(id: Int): Result<Product> = productResult
    override suspend fun searchProducts(query: String): Result<List<Product>> = Result.success(emptyList())
    override suspend fun submitEnquiry(
        productId: Int,
        name: String,
        email: String,
        message: String
    ): Result<Unit> = Result.success(Unit)
}

class ProductDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val repository = FakeProductRepositoryDetails()
    private val productId = 1
    private val savedStateHandle = SavedStateHandle(mapOf("productId" to productId.toString()))

    @Test
    fun productDetailScreen_displaysProductDetailsOnSuccess() {
        val product = Product(
            id = productId,
            title = "Test Product Title",
            price = 199.99,
            description = "This is a detailed description of the test product.",
            category = "Test Category",
            image = "url",
            rating = Rating(4.8, 100)
        )
        repository.productResult = Result.success(product)
        val viewModel = DetailsViewModel(repository, savedStateHandle)

        composeTestRule.setContent {
            ProductDetailScreen(
                onNavigateBack = {},
                onEnquireClick = {},
                viewModel = viewModel
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Product Details").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Product Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("$199.99").assertIsDisplayed()
        composeTestRule.onNodeWithText("This is a detailed description of the test product.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Enquire Now").assertIsDisplayed()
    }

    @Test
    fun productDetailScreen_onEnquireClick_callsCallbackWithProductId() {
        val product = Product(
            id = productId,
            title = "Test Product Title",
            price = 199.99,
            description = "Description",
            category = "Category",
            image = "url",
            rating = Rating(4.0, 10)
        )
        repository.productResult = Result.success(product)
        val viewModel = DetailsViewModel(repository, savedStateHandle)

        var clickedProductId = -1

        composeTestRule.setContent {
            ProductDetailScreen(
                onNavigateBack = {},
                onEnquireClick = { id -> clickedProductId = id },
                viewModel = viewModel
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Enquire Now").performClick()

        assertTrue(clickedProductId == productId)
    }

    @Test
    fun productDetailScreen_onBackClick_callsNavigateBack() {
        val product = Product(
            id = productId,
            title = "Test",
            price = 10.0,
            description = "Desc",
            category = "Cat",
            image = "url",
            rating = Rating(4.0, 10)
        )
        repository.productResult = Result.success(product)
        val viewModel = DetailsViewModel(repository, savedStateHandle)

        var backClicked = false

        composeTestRule.setContent {
            ProductDetailScreen(
                onNavigateBack = { backClicked = true },
                onEnquireClick = {},
                viewModel = viewModel
            )
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()

        assertTrue(backClicked)
    }

    @Test
    fun productDetailScreen_onError_displaysErrorMessage() {
        val errorMessage = "Product details not found"
        repository.productResult = Result.failure(Exception(errorMessage))
        val viewModel = DetailsViewModel(repository, savedStateHandle)

        composeTestRule.setContent {
            ProductDetailScreen(
                onNavigateBack = {},
                onEnquireClick = {},
                viewModel = viewModel
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }
}
