package com.razzaaq.productcatalogmvp.presentation.home

import com.razzaaq.productcatalogmvp.MainDispatcherRule
import com.razzaaq.productcatalogmvp.domain.model.Product
import com.razzaaq.productcatalogmvp.domain.model.Rating
import com.razzaaq.productcatalogmvp.domain.repository.ProductRepository
import com.razzaaq.productcatalogmvp.presentation.UiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: ProductRepository = mockk()

    @Test
    fun `init loads featured products and emits Success state`() = runTest {
        // Arrange
        val mockProducts = listOf(
            Product(1, "Test", 10.0, "Desc", "Cat", "Url", Rating(4.5, 10))
        )
        coEvery { repository.getFeaturedProducts() } returns Result.success(mockProducts)

        // Act
        val viewModel = HomeViewModel(repository)

        // Assert
        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(mockProducts, (state as UiState.Success).data)
    }

    @Test
    fun `init emits Error state on repository failure`() = runTest {
        // Arrange
        val errorMessage = "Network error. Please check your connection."
        coEvery { repository.getFeaturedProducts() } returns Result.failure(Exception(errorMessage))

        // Act
        val viewModel = HomeViewModel(repository)

        // Assert
        val state = viewModel.uiState.value
        assertTrue(state is UiState.Error)
        assertEquals(errorMessage, (state as UiState.Error).message)
    }

    @Test
    fun `selectCategory loads category products and preserves selected category`() = runTest {
        val featuredProducts = listOf(
            Product(1, "Featured", 10.0, "Desc", "Cat", "Url", Rating(4.5, 10))
        )
        val categoryProducts = listOf(
            Product(2, "Jacket", 55.99, "Desc", "men's clothing", "Url", Rating(4.8, 20))
        )
        coEvery { repository.getFeaturedProducts() } returns Result.success(featuredProducts)
        coEvery { repository.getProductsByCategory("men's clothing") } returns Result.success(categoryProducts)

        val viewModel = HomeViewModel(repository)
        viewModel.selectCategory("men's clothing")

        assertEquals("men's clothing", viewModel.selectedCategory.value)
        assertEquals(UiState.Success(categoryProducts), viewModel.uiState.value)
        coVerify { repository.getProductsByCategory("men's clothing") }
    }

    @Test
    fun `selectCategory null restores featured products and clears selection`() = runTest {
        val featuredProducts = listOf(
            Product(1, "Featured", 10.0, "Desc", "Cat", "Url", Rating(4.5, 10))
        )
        coEvery { repository.getFeaturedProducts() } returns Result.success(featuredProducts)
        coEvery { repository.getProductsByCategory("electronics") } returns Result.success(emptyList())

        val viewModel = HomeViewModel(repository)
        viewModel.selectCategory("electronics")
        viewModel.selectCategory(null)

        assertEquals(null, viewModel.selectedCategory.value)
        assertEquals(UiState.Success(featuredProducts), viewModel.uiState.value)
        coVerify(exactly = 2) { repository.getFeaturedProducts() }
    }
}