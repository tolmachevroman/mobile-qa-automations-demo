package com.demo.automation.qa.domain.usecase

import com.demo.automation.qa.domain.model.AuthResult
import com.demo.automation.qa.domain.repository.AuthRepository
import android.util.Patterns

class LoginUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String): AuthResult {
        // Validate email format
        if (!isValidEmail(email)) {
            return AuthResult.Error("Please enter a valid email address")
        }

        // Validate password length
        if (!isValidPassword(password)) {
            return AuthResult.Error("Password must be at least 8 characters")
        }

        return try {
            authRepository.login(email, password)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "An unexpected error occurred")
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }
}