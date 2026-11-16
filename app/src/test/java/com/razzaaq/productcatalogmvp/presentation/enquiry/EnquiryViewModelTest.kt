package com.razzaaq.productcatalogmvp.presentation.enquiry

import androidx.lifecycle.SavedStateHandle
import com.razzaaq.productcatalogmvp.MainDispatcherRule
import com.razzaaq.productcatalogmvp.domain.model.Product
import com.razzaaq.productcatalogmvp.domain.model.Rating
import com.razzaaq.productcatalogmvp.domain.repository.ProductRepository
import com.razzaaq.productcatalogmvp.presentation.UiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class EnquiryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: ProductRepository = mockk()
    private val savedStateHandle: SavedStateHandle = mockk {
        every { get<String>("productId") } returns "123"
    }

    private lateinit var viewModel: EnquiryViewModel

    @Test
    fun `submitEnquiry with empty fields sets error state`() = runTest {
        viewModel = EnquiryViewModel(repository, savedStateHandle)
        viewModel.submitEnquiry()

        val state = viewModel.submitState.value
        assertEquals(SubmitState.Error("Please fill all fields"), state)
    }

    @Test
    fun `submitEnquiry with valid fields success sets success state`() = runTest {
        coEvery { repository.submitEnquiry(123, "John", "john@test.com", "Hello") } returns Result.success(Unit)

        viewModel = EnquiryViewModel(repository, savedStateHandle)
        viewModel.onNameChange("John")
        viewModel.onEmailChange("john@test.com")
        viewModel.onMessageChange("Hello")

        viewModel.submitEnquiry()

        assertEquals(SubmitState.Success, viewModel.submitState.value)
        coVerify { repository.submitEnquiry(123, "John", "john@test.com", "Hello") }
    }

    @Test
    fun `submitEnquiry with valid fields failure sets error state`() = runTest {
        coEvery { repository.submitEnquiry(123, "John", "john@test.com", "Hello") } returns Result.failure(Exception("Failed!"))

        viewModel = EnquiryViewModel(repository, savedStateHandle)
        viewModel.onNameChange("John")
        viewModel.onEmailChange("john@test.com")
        viewModel.onMessageChange("Hello")

        viewModel.submitEnquiry()

        assertEquals(SubmitState.Error("Failed!"), viewModel.submitState.value)
    }

    @Test
    fun `resetState resets submit state to Idle`() = runTest {
        viewModel = EnquiryViewModel(repository, savedStateHandle)
        viewModel.onNameChange("John")
        viewModel.onEmailChange("john@test.com")
        viewModel.onMessageChange("Hello")
        coEvery { repository.submitEnquiry(any(), any(), any(), any()) } returns Result.success(Unit)
        viewModel.submitEnquiry()

        assertEquals(SubmitState.Success, viewModel.submitState.value)

        viewModel.resetState()
        assertEquals(SubmitState.Idle, viewModel.submitState.value)
    }

    @Test
    fun `loadProduct emits product summary state`() = runTest {
        val product = Product(
            123,
            "Jacket",
            55.99,
            "Description",
            "men's clothing",
            "image",
            Rating(4.8, 20)
        )
        coEvery { repository.getProductById(123) } returns Result.success(product)

        viewModel = EnquiryViewModel(repository, savedStateHandle)
        viewModel.loadProduct()

        assertEquals(UiState.Success(product), viewModel.productState.value)
    }

    @Test
    fun `submitEnquiry with invalid email sets validation error and skips repository`() = runTest {
        viewModel = EnquiryViewModel(repository, savedStateHandle)
        viewModel.onNameChange("John")
        viewModel.onEmailChange("not-an-email")
        viewModel.onMessageChange("Hello")

        viewModel.submitEnquiry()

        assertEquals(SubmitState.Error("Enter a valid email address"), viewModel.submitState.value)
        coVerify(exactly = 0) { repository.submitEnquiry(any(), any(), any(), any()) }
    }

    @Test
    fun `editing a field clears validation error`() = runTest {
        viewModel = EnquiryViewModel(repository, savedStateHandle)
        viewModel.submitEnquiry()

        assertEquals(SubmitState.Error("Please fill all fields"), viewModel.submitState.value)

        viewModel.onNameChange("John")

        assertEquals(SubmitState.Idle, viewModel.submitState.value)
    }
}
