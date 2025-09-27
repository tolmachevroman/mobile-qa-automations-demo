package com.demo.automation.qa.fake

import com.demo.automation.qa.data.source.AuthDataSource
import com.demo.automation.qa.domain.model.AuthError
import com.demo.automation.qa.domain.model.AuthResult
import com.demo.automation.qa.domain.model.User
import kotlinx.coroutines.delay

enum class FakeScenario {
    SUCCESS,
    INVALID_CREDENTIALS,
    NETWORK_ERROR,
    SERVER_ERROR_500,
    UNAUTHORIZED_401,
    SERVICE_UNAVAILABLE_503,
    TIMEOUT,
    MALFORMED_RESPONSE,
    RATE_LIMITED_429,
    MAINTENANCE_MODE,
    UNKNOWN_ERROR
}

class FakeAuthDataSource(
    private val scenario: FakeScenario = FakeScenario.SUCCESS,
    private val networkDelay: Long = 100L, // Shorter delay for tests
    private val customErrorMessage: String? = null
) : AuthDataSource {

    // Test credentials - predictable for testing
    private val validCredentials = mapOf(
        "test@company.com" to "Password123!",
        "valid@test.com" to "ValidPass123!",
        "admin@demo.com" to "Admin123!"
    )

    override suspend fun authenticate(email: String, password: String): AuthResult {
        // Simulate network delay (shorter for tests)
        delay(networkDelay)

        return when (scenario) {
            FakeScenario.SUCCESS -> handleSuccess(email, password)
            FakeScenario.INVALID_CREDENTIALS -> handleInvalidCredentials()
            FakeScenario.NETWORK_ERROR -> handleNetworkError()
            FakeScenario.SERVER_ERROR_500 -> handleServerError()
            FakeScenario.UNAUTHORIZED_401 -> handleUnauthorized()
            FakeScenario.SERVICE_UNAVAILABLE_503 -> handleServiceUnavailable()
            FakeScenario.TIMEOUT -> handleTimeout()
            FakeScenario.MALFORMED_RESPONSE -> handleMalformedResponse()
            FakeScenario.RATE_LIMITED_429 -> handleRateLimited()
            FakeScenario.MAINTENANCE_MODE -> handleMaintenanceMode()
            FakeScenario.UNKNOWN_ERROR -> handleUnknownError()
        }
    }

    private fun handleSuccess(email: String, password: String): AuthResult {
        // Check credentials
        val validPassword = validCredentials[email]
        if (validPassword == null || validPassword != password) {
            return AuthResult.Error(AuthError.InvalidCredentials.MESSAGE, "INVALID_CREDENTIALS")
        }

        // Return successful user
        val user = User(
            id = "test-user-${email.hashCode()}",
            email = email,
            accessToken = "test_token_${System.currentTimeMillis()}"
        )
        return AuthResult.Success(user)
    }

    private fun handleInvalidCredentials(): AuthResult {
        return AuthResult.Error(
            customErrorMessage ?: AuthError.InvalidCredentials.MESSAGE,
            "INVALID_CREDENTIALS"
        )
    }

    private fun handleNetworkError(): AuthResult {
        return AuthResult.Error(
            customErrorMessage ?: AuthError.NetworkError.MESSAGE,
            "NETWORK_ERROR"
        )
    }

    private fun handleServerError(): AuthResult {
        return AuthResult.Error(
            customErrorMessage ?: "Internal server error. Please try again later.",
            "SERVER_ERROR_500"
        )
    }

    private fun handleUnauthorized(): AuthResult {
        return AuthResult.Error(
            customErrorMessage ?: "Authentication failed. Please check your credentials.",
            "UNAUTHORIZED_401"
        )
    }

    private fun handleServiceUnavailable(): AuthResult {
        return AuthResult.Error(
            customErrorMessage ?: "Service temporarily unavailable. Please try again later.",
            "SERVICE_UNAVAILABLE_503"
        )
    }

    private suspend fun handleTimeout(): AuthResult {
        // Simulate a timeout scenario with longer delay
        delay(15000) // 15 seconds - would normally timeout
        return AuthResult.Error(
            customErrorMessage ?: "Request timed out. Please check your connection and try again.",
            "TIMEOUT"
        )
    }

    private fun handleMalformedResponse(): AuthResult {
        // Simulate malformed JSON response scenario
        return AuthResult.Error(
            customErrorMessage ?: "Invalid server response. Please try again.",
            "MALFORMED_RESPONSE"
        )
    }

    private fun handleRateLimited(): AuthResult {
        return AuthResult.Error(
            customErrorMessage ?: "Too many attempts. Please try again in a few minutes.",
            "RATE_LIMITED_429"
        )
    }

    private fun handleMaintenanceMode(): AuthResult {
        return AuthResult.Error(
            customErrorMessage ?: "Service is under maintenance. Please try again later.",
            "MAINTENANCE_MODE"
        )
    }

    private fun handleUnknownError(): AuthResult {
        return AuthResult.Error(
            customErrorMessage ?: AuthError.UnknownError.MESSAGE,
            "UNKNOWN_ERROR"
        )
    }

    companion object {
        // Factory methods for common scenarios
        fun success(networkDelay: Long = 100L) =
            FakeAuthDataSource(FakeScenario.SUCCESS, networkDelay)

        fun invalidCredentials(networkDelay: Long = 100L) =
            FakeAuthDataSource(FakeScenario.INVALID_CREDENTIALS, networkDelay)

        fun networkError(networkDelay: Long = 100L) =
            FakeAuthDataSource(FakeScenario.NETWORK_ERROR, networkDelay)

        fun serverError(networkDelay: Long = 100L) =
            FakeAuthDataSource(FakeScenario.SERVER_ERROR_500, networkDelay)

        fun unauthorized(networkDelay: Long = 100L) =
            FakeAuthDataSource(FakeScenario.UNAUTHORIZED_401, networkDelay)

        fun serviceUnavailable(networkDelay: Long = 100L) =
            FakeAuthDataSource(FakeScenario.SERVICE_UNAVAILABLE_503, networkDelay)

        fun rateLimited(networkDelay: Long = 100L) =
            FakeAuthDataSource(FakeScenario.RATE_LIMITED_429, networkDelay)

        fun maintenanceMode(networkDelay: Long = 100L) =
            FakeAuthDataSource(FakeScenario.MAINTENANCE_MODE, networkDelay)

        fun malformedResponse(networkDelay: Long = 100L) =
            FakeAuthDataSource(FakeScenario.MALFORMED_RESPONSE, networkDelay)
    }
}