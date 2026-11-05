package com.razzaaq.productcatalogmvp.presentation.enquiry

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.SavedStateHandle
import com.razzaaq.productcatalogmvp.domain.model.Product
import com.razzaaq.productcatalogmvp.domain.repository.ProductRepository
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FakeProductRepositoryEnquiry : ProductRepository {
    var submitResult: Result<Unit> = Result.success(Unit)

    override suspend fun getFeaturedProducts(): Result<List<Product>> = Result.success(emptyList())
    override suspend fun getProductsByCategory(category: String): Result<List<Product>> = Result.success(emptyList())
    override suspend fun getProductById(id: Int): Result<Product> = Result.failure(Exception())

    override suspend fun searchProducts(query: String): Result<List<Product>> {
        return Result.success(emptyList())
    }

    override suspend fun submitEnquiry(
        productId: Int,
        name: String,
        email: String,
        message: String
    ): Result<Unit> {
        return submitResult
    }
}

class EnquiryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val repository = FakeProductRepositoryEnquiry()
    private val savedStateHandle = SavedStateHandle(mapOf("productId" to "123"))

    @Test
    fun enquiryScreen_initialState_displaysAllFields() {
        val viewModel = EnquiryViewModel(repository, savedStateHandle)
        composeTestRule.setContent {
            EnquiryScreen(onNavigateBack = {}, viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Product Enquiry").assertIsDisplayed()
        composeTestRule.onNode(hasText("Name") and hasSetTextAction()).assertIsDisplayed()
        composeTestRule.onNode(hasText("Email") and hasSetTextAction()).assertIsDisplayed()
        composeTestRule.onNode(hasText("Message") and hasSetTextAction()).assertIsDisplayed()
        composeTestRule.onNodeWithText("Submit Enquiry").assertIsDisplayed()
    }

    @Test
    fun enquiryScreen_submitEmptyFields_showsErrorStateButKeepsFields() {
        val viewModel = EnquiryViewModel(repository, savedStateHandle)
        composeTestRule.setContent {
            EnquiryScreen(onNavigateBack = {}, viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Submit Enquiry").performClick()

        // Ensure fields are still displayed and there's no crash
        composeTestRule.onNode(hasText("Name") and hasSetTextAction()).assertIsDisplayed()
        composeTestRule.onNodeWithText("Submit Enquiry").assertIsDisplayed()
    }

    @Test
    fun enquiryScreen_submitValidFields_callsNavigateBackOnSuccess() {
        repository.submitResult = Result.success(Unit)

        var backPressed = false
        val viewModel = EnquiryViewModel(repository, savedStateHandle)

        composeTestRule.setContent {
            EnquiryScreen(onNavigateBack = { backPressed = true }, viewModel = viewModel)
        }

        // Fill out form
        composeTestRule.onNode(hasText("Name") and hasSetTextAction()).performTextInput("John")
        composeTestRule.onNode(hasText("Email") and hasSetTextAction()).performTextInput("john@test.com")
        composeTestRule.onNode(hasText("Message") and hasSetTextAction()).performTextInput("Msg")

        // Submit
        composeTestRule.onNodeWithText("Submit Enquiry").performClick()

        // Wait for coroutines and UI to settle
        composeTestRule.waitForIdle()

        assertTrue(backPressed)
    }
}
