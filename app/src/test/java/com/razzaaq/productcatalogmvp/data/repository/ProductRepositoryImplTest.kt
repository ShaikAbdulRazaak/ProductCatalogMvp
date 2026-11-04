package com.razzaaq.productcatalogmvp.data.repository

import com.razzaaq.productcatalogmvp.data.remote.EnquiryRequest
import com.razzaaq.productcatalogmvp.data.remote.ProductDto
import com.razzaaq.productcatalogmvp.data.remote.RatingDto
import com.razzaaq.productcatalogmvp.data.remote.StoreApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


class ProductRepositoryImplTest {

    private lateinit var api: StoreApi
    private lateinit var repository: ProductRepositoryImpl

    @Before
    fun setUp() {
        api = mockk()
        repository = ProductRepositoryImpl(api)
    }

    @Test
    fun `getFeaturedProducts returns mapped domain models on success`() = runTest {
        val mockDto = ProductDto(1, "Title", 10.0, "Desc", "Cat", "url", RatingDto(4.5, 10))
        coEvery { api.getFeaturedProducts() } returns listOf(mockDto)

        val result = repository.getFeaturedProducts()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.first()?.id)
        assertEquals(4.5, result.getOrNull()?.first()?.rating?.rate)
    }

    @Test
    fun `getFeaturedProducts returns failure with network message on IOException`() = runTest {
        coEvery { api.getFeaturedProducts() } throws IOException("No internet")

        val result = repository.getFeaturedProducts()

        assertTrue(result.isFailure)
        assertEquals(
            "Network error. Please check your connection.",
            result.exceptionOrNull()?.message
        )
    }

    @Test
    fun `submitEnquiry returns success when API call completes`() = runTest {
        coEvery { api.submitEnquiry(any()) } returns Unit

        val result = repository.submitEnquiry(1, "John", "test@test.com", "Hello")

        assertTrue(result.isSuccess)
        coVerify {
            api.submitEnquiry(EnquiryRequest(1, "John", "test@test.com", "Hello"))
        }
    }

    @Test
    fun `submitEnquiry returns failure with server message on HttpException`() = runTest {
        // Mocking Retrofit's HttpException requires a dummy error response
        val errorResponse = Response.error<Unit>(500, "Server Error".toResponseBody(null))
        coEvery { api.submitEnquiry(any()) } throws HttpException(errorResponse)

        val result = repository.submitEnquiry(1, "John", "test@test.com", "Hello")

        assertTrue(result.isFailure)
        assertEquals(
            "Server error (500). Please try again later.",
            result.exceptionOrNull()?.message
        )
    }
}