package com.razzaaq.productcatalogmvp.presentation.details

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

class DetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: ProductRepository = mockk()

    @Test
    fun `init loads product details and emits Success state for valid ID`() = runTest {
        // Arrange
        val productId = 1
        val savedStateHandle = SavedStateHandle(mapOf("productId" to productId.toString()))
        val mockProduct = Product(productId, "Test", 10.0, "Desc", "Cat", "Url", Rating(4.5, 10))
        coEvery { repository.getProductById(productId) } returns Result.success(mockProduct)

        // Act
        val viewModel = DetailsViewModel(repository, savedStateHandle)

        // Assert
        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(mockProduct, (state as UiState.Success).data)
    }

    @Test
    fun `init emits Error state on repository failure for valid ID`() = runTest {
        // Arrange
        val productId = 1
        val savedStateHandle = SavedStateHandle(mapOf("productId" to productId.toString()))
        val errorMessage = "Product not found"
        coEvery { repository.getProductById(productId) } returns Result.failure(Exception(errorMessage))

        // Act
        val viewModel = DetailsViewModel(repository, savedStateHandle)

        // Assert
        val state = viewModel.uiState.value
        assertTrue(state is UiState.Error)
        assertEquals(errorMessage, (state as UiState.Error).message)
    }

    @Test
    fun `init emits Error state when productId is invalid (0)`() = runTest {
        // Arrange
        val savedStateHandle = SavedStateHandle(mapOf("productId" to "invalid")) // will default to 0

        // Act
        val viewModel = DetailsViewModel(repository, savedStateHandle)

        // Assert
        val state = viewModel.uiState.value
        assertTrue(state is UiState.Error)
        assertEquals("Invalid Product ID", (state as UiState.Error).message)
    }
}
