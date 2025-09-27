package com.demo.automation.qa.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.automation.qa.domain.model.AuthResult
import com.demo.automation.qa.domain.usecase.LoginUseCase
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isLoginButtonEnabled: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null
)

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun updateEmail(email: String) {
        uiState = uiState.copy(
            email = email,
            emailError = validateEmail(email),
            errorMessage = null
        )
        updateLoginButtonState()
    }

    fun updatePassword(password: String) {
        uiState = uiState.copy(
            password = password,
            passwordError = validatePassword(password),
            errorMessage = null
        )
        updateLoginButtonState()
    }

    fun togglePasswordVisibility() {
        uiState = uiState.copy(
            isPasswordVisible = !uiState.isPasswordVisible
        )
    }

    fun login() {
        if (!uiState.isLoginButtonEnabled || uiState.isLoading) return

        uiState = uiState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = loginUseCase(uiState.email, uiState.password)

            when (result) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        isLoginSuccessful = true,
                        errorMessage = null
                    )
                }

                is AuthResult.Error -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isEmpty() -> "Email is required"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches() -> "Please enter a valid email address"

            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() -> "Password is required"
            password.length < 8 -> "Password must be at least 8 characters"
            else -> null
        }
    }

    private fun updateLoginButtonState() {
        uiState = uiState.copy(
            isLoginButtonEnabled = uiState.email.isNotEmpty() &&
                    uiState.password.isNotEmpty() &&
                    uiState.emailError == null &&
                    uiState.passwordError == null
        )
    }
}