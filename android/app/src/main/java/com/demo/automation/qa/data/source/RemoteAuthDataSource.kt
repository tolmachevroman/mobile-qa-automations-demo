package com.demo.automation.qa.data.source

import com.demo.automation.qa.domain.model.AuthError
import com.demo.automation.qa.domain.model.AuthResult
import com.demo.automation.qa.domain.model.User
import kotlinx.coroutines.delay
import java.util.UUID

class RemoteAuthDataSource : AuthDataSource {

    // Mock credentials for demo purposes
    private val validCredentials = mapOf(
        "test@company.com" to "Password123!",
        "admin@demo.com" to "Admin123!",
        "user@example.com" to "User123!"
    )

    override suspend fun authenticate(email: String, password: String): AuthResult {
        // Simulate network delay
        delay(1000)

        // Simulate network error (10% chance)
        if (Math.random() < 0.1) {
            return AuthResult.Error(AuthError.NetworkError.MESSAGE, "NETWORK_ERROR")
        }

        // Check credentials
        val validPassword = validCredentials[email]
        if (validPassword == null || validPassword != password) {
            return AuthResult.Error(AuthError.InvalidCredentials.MESSAGE, "INVALID_CREDENTIALS")
        }

        // Return successful user
        val user = User(
            id = UUID.randomUUID().toString(),
            email = email,
            accessToken = "mock_token_${System.currentTimeMillis()}"
        )
        return AuthResult.Success(user)
    }
}