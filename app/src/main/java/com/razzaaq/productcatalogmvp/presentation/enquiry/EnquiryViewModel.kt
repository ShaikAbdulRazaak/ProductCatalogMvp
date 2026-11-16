package com.razzaaq.productcatalogmvp.presentation.enquiry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.razzaaq.productcatalogmvp.domain.repository.ProductRepository
import com.razzaaq.productcatalogmvp.domain.model.Product
import com.razzaaq.productcatalogmvp.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnquiryViewModel @Inject constructor(
    private val repository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: Int = savedStateHandle.get<String>("productId")?.toIntOrNull() ?: 0

    private val _productState = MutableStateFlow<UiState<Product>>(UiState.Loading)
    val productState: StateFlow<UiState<Product>> = _productState.asStateFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message.asStateFlow()

    private val _submitState = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val submitState: StateFlow<SubmitState> = _submitState.asStateFlow()

    fun loadProduct() {
        viewModelScope.launch {
            repository.getProductById(productId).fold(
                onSuccess = { _productState.value = UiState.Success(it) },
                onFailure = { _productState.value = UiState.Error(it.message ?: "Unable to load product") }
            )
        }
    }

    fun onNameChange(newName: String) {
        _name.value = newName
        clearValidationError()
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        clearValidationError()
    }

    fun onMessageChange(newMessage: String) {
        _message.value = newMessage
        clearValidationError()
    }

    fun submitEnquiry() {
        if (name.value.isBlank() || email.value.isBlank() || message.value.isBlank()) {
            _submitState.value = SubmitState.Error("Please fill all fields")
            return
        }
        if (!EMAIL_PATTERN.matches(email.value.trim())) {
            _submitState.value = SubmitState.Error("Enter a valid email address")
            return
        }

        _submitState.value = SubmitState.Loading
        viewModelScope.launch {
            repository.submitEnquiry(productId, name.value.trim(), email.value.trim(), message.value.trim()).fold(
                onSuccess = { _submitState.value = SubmitState.Success },
                onFailure = { _submitState.value = SubmitState.Error(it.message ?: "Failed to submit enquiry") }
            )
        }
    }

    fun resetState() {
        _submitState.value = SubmitState.Idle
    }

    private fun clearValidationError() {
        if (_submitState.value is SubmitState.Error) {
            _submitState.value = SubmitState.Idle
        }
    }
}

internal val EMAIL_PATTERN = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

sealed class SubmitState {
    data object Idle : SubmitState()
    data object Loading : SubmitState()
    data object Success : SubmitState()
    data class Error(val message: String) : SubmitState()
}