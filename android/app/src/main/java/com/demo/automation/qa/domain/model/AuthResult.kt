package com.demo.automation.qa.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed class AuthResult {
    @Serializable
    data class Success(val user: User) : AuthResult()

    @Serializable
    data class Error(val message: String, val errorCode: String? = null) : AuthResult()
}

// Domain error types - no need to extend Exception
@Serializable
sealed class AuthError {
    @Serializable
    object InvalidCredentials : AuthError() {
        const val MESSAGE = "Invalid email or password"
    }

    @Serializable
    object NetworkError : AuthError() {
        const val MESSAGE = "Unable to connect. Please check your internet connection."
    }

    @Serializable
    object UnknownError : AuthError() {
        const val MESSAGE = "An unexpected error occurred. Please try again."
    }
}