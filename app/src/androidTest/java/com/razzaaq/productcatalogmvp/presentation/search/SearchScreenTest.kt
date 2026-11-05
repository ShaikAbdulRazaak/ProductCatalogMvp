package com.razzaaq.productcatalogmvp.presentation.search

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.razzaaq.productcatalogmvp.domain.model.Product
import com.razzaaq.productcatalogmvp.domain.model.Rating
import com.razzaaq.productcatalogmvp.domain.repository.ProductRepository
import org.junit.Rule
import org.junit.Test

class FakeProductRepository : ProductRepository {
    var searchResult: Result<List<Product>> = Result.success(emptyList())

    override suspend fun getFeaturedProducts(): Result<List<Product>> = Result.success(emptyList())
    override suspend fun getProductsByCategory(category: String): Result<List<Product>> = Result.success(emptyList())
    override suspend fun getProductById(id: Int): Result<Product> = Result.failure(Exception())

    override suspend fun searchProducts(query: String): Result<List<Product>> {
        return searchResult
    }

    override suspend fun submitEnquiry(
        productId: Int,
        name: String,
        email: String,
        message: String
    ): Result<Unit> {
        return Result.success(Unit)
    }
}

class SearchScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val repository = FakeProductRepository()

    @Test
    fun searchScreen_initialState_displaysEmptySearch() {
        val viewModel = SearchViewModel(repository)
        composeTestRule.setContent {
            SearchScreen(onProductClick = {}, viewModel = viewModel)
        }

        composeTestRule.onNode(hasSetTextAction()).assertIsDisplayed()
        composeTestRule.onNodeWithText("No products found").assertDoesNotExist()
    }

    @Test
    fun searchScreen_emptySearchQuery_displaysNoProductsFound_whenNoResults() {
        repository.searchResult = Result.success(emptyList())
        val viewModel = SearchViewModel(repository)

        composeTestRule.setContent {
            SearchScreen(onProductClick = {}, viewModel = viewModel)
        }

        composeTestRule.onNode(hasSetTextAction()).performTextInput("test")

        composeTestRule.onNodeWithText("No products found").assertIsDisplayed()
    }

    @Test
    fun searchScreen_withResults_displaysProductTiles() {
        val products = listOf(
            Product(1, "Test Product 1", 10.0, "Desc 1", "Cat", "img", Rating(4.5, 10)),
            Product(2, "Test Product 2", 20.0, "Desc 2", "Cat", "img", Rating(4.0, 5))
        )
        repository.searchResult = Result.success(products)

        val viewModel = SearchViewModel(repository)

        composeTestRule.setContent {
            SearchScreen(onProductClick = {}, viewModel = viewModel)
        }

        composeTestRule.onNode(hasSetTextAction()).performTextInput("Test")

        composeTestRule.onNodeWithText("Test Product 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Product 2").assertIsDisplayed()
    }

    @Test
    fun searchScreen_errorState_displaysErrorMessage() {
        val errorMessage = "Network Error"
        repository.searchResult = Result.failure(Exception(errorMessage))

        val viewModel = SearchViewModel(repository)

        composeTestRule.setContent {
            SearchScreen(onProductClick = {}, viewModel = viewModel)
        }

        composeTestRule.onNode(hasSetTextAction()).performTextInput("Test")

        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }
}
