package com.razzaaq.productcatalogmvp.presentation.search

import com.razzaaq.productcatalogmvp.MainDispatcherRule
import com.razzaaq.productcatalogmvp.domain.model.Product
import com.razzaaq.productcatalogmvp.domain.model.Rating
import com.razzaaq.productcatalogmvp.domain.repository.ProductRepository
import com.razzaaq.productcatalogmvp.presentation.UiState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: ProductRepository = mockk()
    private lateinit var viewModel: SearchViewModel

    @Test
    fun `onSearchQueryChange with blank query emits success with empty list`() = runTest {
        viewModel = SearchViewModel(repository)
        viewModel.onSearchQueryChange("   ")

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertTrue((state as UiState.Success).data.isEmpty())
    }

    @Test
    fun `onSearchQueryChange with valid query emits loading then success`() = runTest {
        val products = listOf(
            Product(1, "Title", 10.0, "Desc", "Cat", "img", Rating(4.5, 10))
        )
        coEvery { repository.searchProducts("test") } returns Result.success(products)

        viewModel = SearchViewModel(repository)
        viewModel.onSearchQueryChange("test")

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(products, (state as UiState.Success).data)
    }

    @Test
    fun `onSearchQueryChange with valid query emits loading then error on failure`() = runTest {
        val errorMessage = "Network Error"
        coEvery { repository.searchProducts("test") } returns Result.failure(Exception(errorMessage))

        viewModel = SearchViewModel(repository)
        viewModel.onSearchQueryChange("test")

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Error)
        assertEquals(errorMessage, (state as UiState.Error).message)
    }
}
