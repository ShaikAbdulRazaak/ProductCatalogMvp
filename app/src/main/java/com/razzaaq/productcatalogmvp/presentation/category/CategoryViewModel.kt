package com.razzaaq.productcatalogmvp.presentation.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.razzaaq.productcatalogmvp.domain.model.Product
import com.razzaaq.productcatalogmvp.domain.repository.ProductRepository
import com.razzaaq.productcatalogmvp.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Product>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Product>>> = _uiState.asStateFlow()

    val categoryName: String = savedStateHandle.get<String>("categoryId") ?: "Category"

    init {
        loadCategoryProducts(categoryName)
    }

    private fun loadCategoryProducts(category: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getProductsByCategory(category).fold(
                onSuccess = { products ->
                    _uiState.value = UiState.Success(products)
                },
                onFailure = { exception ->
                    _uiState.value = UiState.Error(exception.message ?: "Unknown error occurred")
                }
            )
        }
    }
}