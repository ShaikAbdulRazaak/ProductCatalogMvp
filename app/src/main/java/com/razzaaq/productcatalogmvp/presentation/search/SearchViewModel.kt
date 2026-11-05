package com.razzaaq.productcatalogmvp.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.razzaaq.productcatalogmvp.domain.model.Product
import com.razzaaq.productcatalogmvp.domain.repository.ProductRepository
import com.razzaaq.productcatalogmvp.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Product>>>(UiState.Success(emptyList()))
    val uiState: StateFlow<UiState<List<Product>>> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var searchJob: Job? = null

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()

        if (query.isBlank()) {
            _uiState.value = UiState.Success(emptyList())
        } else {
            searchJob = searchProducts(query.trim())
        }
    }

    private fun searchProducts(query: String): Job = viewModelScope.launch {
        _uiState.value = UiState.Loading
        repository.searchProducts(query).fold(
            onSuccess = { products ->
                if (_searchQuery.value.trim() == query) {
                    _uiState.value = UiState.Success(products)
                }
            },
            onFailure = { error ->
                if (_searchQuery.value.trim() == query) {
                    _uiState.value = UiState.Error(error.message ?: "Unable to search products")
                }
            }
        )
    }
}
