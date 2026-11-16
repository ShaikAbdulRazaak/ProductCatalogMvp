package com.razzaaq.productcatalogmvp.presentation.category

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.SavedStateHandle
import com.razzaaq.productcatalogmvp.domain.model.Product
import com.razzaaq.productcatalogmvp.domain.model.Rating
import com.razzaaq.productcatalogmvp.domain.repository.ProductRepository
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FakeProductRepositoryCategory : ProductRepository {
    var categoryProductsResult: Result<List<Product>> = Result.success(emptyList())

    override suspend fun getFeaturedProducts(): Result<List<Product>> = Result.success(emptyList())
    override suspend fun getProductsByCategory(category: String): Result<List<Product>> = categoryProductsResult
    override suspend fun getProductById(id: Int): Result<Product> = Result.failure(Exception())
    override suspend fun searchProducts(query: String): Result<List<Product>> = Result.success(emptyList())
    override suspend fun submitEnquiry(
        productId: Int,
        name: String,
        email: String,
        message: String
    ): Result<Unit> = Result.success(Unit)
}

class CategoryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val repository = FakeProductRepositoryCategory()
    private val categoryName = "electronics"
    private val savedStateHandle = SavedStateHandle(mapOf("categoryId" to categoryName))

    @Test
    fun categoryScreen_displaysTopBarWithCategoryName() {
        repository.categoryProductsResult = Result.success(emptyList())
        val viewModel = CategoryViewModel(repository, savedStateHandle)

        composeTestRule.setContent {
            CategoryScreen(
                onProductClick = {},
                onNavigateBack = {},
                viewModel = viewModel
            )
        }

        // Check if category name (capitalized) is displayed
        composeTestRule.onNodeWithText("Electronics").assertIsDisplayed()
        // Check back button
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun categoryScreen_loadsProducts_displaysProductList() {
        val products = listOf(
            Product(1, "Laptop", 999.99, "Fast laptop", categoryName, "url", Rating(4.5, 10)),
            Product(2, "Phone", 499.99, "Smart phone", categoryName, "url", Rating(4.0, 5))
        )
        repository.categoryProductsResult = Result.success(products)
        val viewModel = CategoryViewModel(repository, savedStateHandle)

        composeTestRule.setContent {
            CategoryScreen(
                onProductClick = {},
                onNavigateBack = {},
                viewModel = viewModel
            )
        }

        // Wait for list to render
        composeTestRule.waitForIdle()

        // Check products are displayed
        composeTestRule.onNodeWithText("Laptop").assertIsDisplayed()
        composeTestRule.onNodeWithText("$999.99").assertIsDisplayed()
        composeTestRule.onNodeWithText("Phone").assertIsDisplayed()
    }

    @Test
    fun categoryScreen_onProductClick_callsCallback() {
        val products = listOf(
            Product(1, "Laptop", 999.99, "Fast laptop", categoryName, "url", Rating(4.5, 10))
        )
        repository.categoryProductsResult = Result.success(products)
        val viewModel = CategoryViewModel(repository, savedStateHandle)

        var clickedProductId = -1

        composeTestRule.setContent {
            CategoryScreen(
                onProductClick = { id -> clickedProductId = id },
                onNavigateBack = {},
                viewModel = viewModel
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Laptop").performClick()

        assertTrue(clickedProductId == 1)
    }

    @Test
    fun categoryScreen_onBackClick_callsNavigateBack() {
        repository.categoryProductsResult = Result.success(emptyList())
        val viewModel = CategoryViewModel(repository, savedStateHandle)

        var backClicked = false

        composeTestRule.setContent {
            CategoryScreen(
                onProductClick = {},
                onNavigateBack = { backClicked = true },
                viewModel = viewModel
            )
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()

        assertTrue(backClicked)
    }

    @Test
    fun categoryScreen_onError_displaysErrorMessage() {
        val errorMessage = "Failed to load category"
        repository.categoryProductsResult = Result.failure(Exception(errorMessage))
        val viewModel = CategoryViewModel(repository, savedStateHandle)

        composeTestRule.setContent {
            CategoryScreen(
                onProductClick = {},
                onNavigateBack = {},
                viewModel = viewModel
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }
}
