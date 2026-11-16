package com.razzaaq.productcatalogmvp.presentation.category

import androidx.lifecycle.SavedStateHandle
import com.razzaaq.productcatalogmvp.MainDispatcherRule
import com.razzaaq.productcatalogmvp.domain.model.Product
import com.razzaaq.productcatalogmvp.domain.model.Rating
import com.razzaaq.productcatalogmvp.domain.repository.ProductRepository
import com.razzaaq.productcatalogmvp.presentation.UiState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CategoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: ProductRepository = mockk()

    @Test
    fun `init loads products by category and emits Success state`() = runTest {
        // Arrange
        val categoryName = "electronics"
        val savedStateHandle = SavedStateHandle(mapOf("categoryId" to categoryName))
        val mockProducts = listOf(
            Product(1, "Test Product", 10.0, "Desc", categoryName, "Url", Rating(4.5, 10))
        )
        coEvery { repository.getProductsByCategory(categoryName) } returns Result.success(mockProducts)

        // Act
        val viewModel = CategoryViewModel(repository, savedStateHandle)

        // Assert
        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(mockProducts, (state as UiState.Success).data)
        assertEquals(categoryName, viewModel.categoryName)
    }

    @Test
    fun `init emits Error state on repository failure`() = runTest {
        // Arrange
        val categoryName = "jewelery"
        val savedStateHandle = SavedStateHandle(mapOf("categoryId" to categoryName))
        val errorMessage = "Network error"
        coEvery { repository.getProductsByCategory(categoryName) } returns Result.failure(Exception(errorMessage))

        // Act
        val viewModel = CategoryViewModel(repository, savedStateHandle)

        // Assert
        val state = viewModel.uiState.value
        assertTrue(state is UiState.Error)
        assertEquals(errorMessage, (state as UiState.Error).message)
        assertEquals(categoryName, viewModel.categoryName)
    }
}
