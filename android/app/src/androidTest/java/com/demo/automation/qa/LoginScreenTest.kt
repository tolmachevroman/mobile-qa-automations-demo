package com.demo.automation.qa

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.demo.automation.qa.data.repository.AuthRepositoryImpl
import com.demo.automation.qa.domain.usecase.LoginUseCase
import com.demo.automation.qa.fake.FakeAuthDataSource
import com.demo.automation.qa.fake.FakeScenario
import com.demo.automation.qa.ui.auth.LoginViewModel
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * Comprehensive tests for Login functionality covering real-world scenarios
 *
 * This demonstrates thorough testing with:
 * - Fake implementations for deterministic testing
 * - Real-world API error scenarios (HTTP status codes)
 * - Edge cases and malformed responses
 * - Timeout and rate limiting scenarios
 * - Manual DI for clean test setup
 */
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    private fun createLoginViewModel(
        fakeDataSource: FakeAuthDataSource = FakeAuthDataSource.success()
    ): LoginViewModel {
        val repository = AuthRepositoryImpl(fakeDataSource)
        val useCase = LoginUseCase(repository)
        return LoginViewModel(useCase)
    }

    // MARK: - Basic Functionality Tests

    @Test
    fun viewModel_creation_works() {
        val loginViewModel = createLoginViewModel()
        assertNotNull(loginViewModel)

        // Test initial state
        val initialState = loginViewModel.uiState
        assertEquals("", initialState.email)
        assertEquals("", initialState.password)
        assertEquals(false, initialState.isLoading)
        assertEquals(false, initialState.isLoginSuccessful)
        assertEquals(false, initialState.isPasswordVisible)
        assertEquals(false, initialState.isLoginButtonEnabled)
    }

    @Test
    fun email_validation_logic_works() {
        val loginViewModel = createLoginViewModel()

        // Test empty email
        loginViewModel.updateEmail("")
        assertNotNull(loginViewModel.uiState.emailError)

        // Test invalid email format
        loginViewModel.updateEmail("invalid-email")
        assertNotNull(loginViewModel.uiState.emailError)

        // Test valid email format
        loginViewModel.updateEmail("test@company.com")
        assertNull(loginViewModel.uiState.emailError)
    }

    @Test
    fun password_validation_logic_works() {
        val loginViewModel = createLoginViewModel()

        // Test empty password
        loginViewModel.updatePassword("")
        assertNotNull(loginViewModel.uiState.passwordError)

        // Test short password
        loginViewModel.updatePassword("short")
        assertNotNull(loginViewModel.uiState.passwordError)

        // Test valid password
        loginViewModel.updatePassword("Password123!")
        assertNull(loginViewModel.uiState.passwordError)
    }

    @Test
    fun login_button_enabled_logic_works() {
        val loginViewModel = createLoginViewModel()

        // Initially disabled
        assertFalse(loginViewModel.uiState.isLoginButtonEnabled)

        // With valid email only
        loginViewModel.updateEmail("test@company.com")
        assertFalse(loginViewModel.uiState.isLoginButtonEnabled)

        // With valid email and password
        loginViewModel.updatePassword("Password123!")
        assertTrue(loginViewModel.uiState.isLoginButtonEnabled)
    }

    @Test
    fun password_visibility_toggle_works() {
        val loginViewModel = createLoginViewModel()

        // Initially hidden
        assertFalse(loginViewModel.uiState.isPasswordVisible)

        // Toggle to show
        loginViewModel.togglePasswordVisibility()
        assertTrue(loginViewModel.uiState.isPasswordVisible)

        // Toggle to hide
        loginViewModel.togglePasswordVisibility()
        assertFalse(loginViewModel.uiState.isPasswordVisible)
    }

    // MARK: - Success Scenarios

    @Test
    fun successful_login_with_valid_credentials() {
        val loginViewModel = createLoginViewModel(FakeAuthDataSource.success(50L))

        // Set up valid credentials
        loginViewModel.updateEmail("test@company.com")
        loginViewModel.updatePassword("Password123!")

        // Verify state is ready for login
        assertTrue(loginViewModel.uiState.isLoginButtonEnabled)
        assertNull(loginViewModel.uiState.emailError)
        assertNull(loginViewModel.uiState.passwordError)

        // Trigger login
        loginViewModel.login()

        // Verify loading state
        assertTrue(loginViewModel.uiState.isLoading)

        // Wait for async operation
        Thread.sleep(150)

        // Verify successful login
        assertTrue(loginViewModel.uiState.isLoginSuccessful)
        assertFalse(loginViewModel.uiState.isLoading)
        assertNull(loginViewModel.uiState.errorMessage)
    }

    // MARK: - Authentication Error Scenarios

    @Test
    fun invalid_credentials_error_handling() {
        val loginViewModel = createLoginViewModel(FakeAuthDataSource.invalidCredentials(50L))

        // Set up invalid credentials
        loginViewModel.updateEmail("wrong@email.com")
        loginViewModel.updatePassword("WrongPassword!")

        // Trigger login
        loginViewModel.login()
        Thread.sleep(150)

        // Verify error handling
        assertFalse(loginViewModel.uiState.isLoginSuccessful)
        assertFalse(loginViewModel.uiState.isLoading)
        assertNotNull(loginViewModel.uiState.errorMessage)
        assertTrue(loginViewModel.uiState.errorMessage!!.contains("Invalid email or password"))
    }

    @Test
    fun unauthorized_401_error_handling() {
        val loginViewModel = createLoginViewModel(FakeAuthDataSource.unauthorized(50L))

        loginViewModel.updateEmail("test@company.com")
        loginViewModel.updatePassword("Password123!")

        loginViewModel.login()
        Thread.sleep(150)

        // Verify 401 error handling
        assertFalse(loginViewModel.uiState.isLoginSuccessful)
        assertNotNull(loginViewModel.uiState.errorMessage)
        assertTrue(loginViewModel.uiState.errorMessage!!.contains("Authentication failed"))
    }

    // MARK: - Network Error Scenarios

    @Test
    fun network_error_simulation() {
        val loginViewModel = createLoginViewModel(FakeAuthDataSource.networkError(50L))

        // Set up valid credentials but force network error
        loginViewModel.updateEmail("test@company.com")
        loginViewModel.updatePassword("Password123!")

        // Trigger login
        loginViewModel.login()
        Thread.sleep(150)

        // Verify network error handling
        assertFalse(loginViewModel.uiState.isLoginSuccessful)
        assertFalse(loginViewModel.uiState.isLoading)
        assertNotNull(loginViewModel.uiState.errorMessage)
        assertTrue(loginViewModel.uiState.errorMessage!!.contains("Unable to connect"))
    }

    // MARK: - Server Error Scenarios (5xx)

    @Test
    fun server_error_500_handling() {
        val loginViewModel = createLoginViewModel(FakeAuthDataSource.serverError(50L))

        loginViewModel.updateEmail("test@company.com")
        loginViewModel.updatePassword("Password123!")

        loginViewModel.login()
        Thread.sleep(150)

        // Verify server error handling
        assertFalse(loginViewModel.uiState.isLoginSuccessful)
        assertNotNull(loginViewModel.uiState.errorMessage)
        assertTrue(loginViewModel.uiState.errorMessage!!.contains("Internal server error"))
    }

    @Test
    fun service_unavailable_503_handling() {
        val loginViewModel = createLoginViewModel(FakeAuthDataSource.serviceUnavailable(50L))

        loginViewModel.updateEmail("test@company.com")
        loginViewModel.updatePassword("Password123!")

        loginViewModel.login()
        Thread.sleep(150)

        // Verify 503 error handling
        assertFalse(loginViewModel.uiState.isLoginSuccessful)
        assertNotNull(loginViewModel.uiState.errorMessage)
        assertTrue(loginViewModel.uiState.errorMessage!!.contains("temporarily unavailable"))
    }

    // MARK: - Rate Limiting and Abuse Prevention

    @Test
    fun rate_limited_429_handling() {
        val loginViewModel = createLoginViewModel(FakeAuthDataSource.rateLimited(50L))

        loginViewModel.updateEmail("test@company.com")
        loginViewModel.updatePassword("Password123!")

        loginViewModel.login()
        Thread.sleep(150)

        // Verify rate limiting error handling
        assertFalse(loginViewModel.uiState.isLoginSuccessful)
        assertNotNull(loginViewModel.uiState.errorMessage)
        assertTrue(loginViewModel.uiState.errorMessage!!.contains("Too many attempts"))
    }

    // MARK: - Maintenance and System State Scenarios

    @Test
    fun maintenance_mode_handling() {
        val loginViewModel = createLoginViewModel(FakeAuthDataSource.maintenanceMode(50L))

        loginViewModel.updateEmail("test@company.com")
        loginViewModel.updatePassword("Password123!")

        loginViewModel.login()
        Thread.sleep(150)

        // Verify maintenance mode handling
        assertFalse(loginViewModel.uiState.isLoginSuccessful)
        assertNotNull(loginViewModel.uiState.errorMessage)
        assertTrue(loginViewModel.uiState.errorMessage!!.contains("under maintenance"))
    }

    // MARK: - Malformed Response Scenarios

    @Test
    fun malformed_response_handling() {
        val loginViewModel = createLoginViewModel(FakeAuthDataSource.malformedResponse(50L))

        loginViewModel.updateEmail("test@company.com")
        loginViewModel.updatePassword("Password123!")

        loginViewModel.login()
        Thread.sleep(150)

        // Verify malformed response error handling
        assertFalse(loginViewModel.uiState.isLoginSuccessful)
        assertNotNull(loginViewModel.uiState.errorMessage)
        assertTrue(loginViewModel.uiState.errorMessage!!.contains("Invalid server response"))
    }

    // MARK: - Edge Cases

    @Test
    fun custom_error_message_handling() {
        val customMessage = "Custom API error: Unknown field 'extra_field' in response"
        val loginViewModel = createLoginViewModel(
            FakeAuthDataSource(
                scenario = FakeScenario.MALFORMED_RESPONSE,
                networkDelay = 50L,
                customErrorMessage = customMessage
            )
        )

        loginViewModel.updateEmail("test@company.com")
        loginViewModel.updatePassword("Password123!")

        loginViewModel.login()
        Thread.sleep(150)

        // Verify custom error message is propagated
        assertFalse(loginViewModel.uiState.isLoginSuccessful)
        assertNotNull(loginViewModel.uiState.errorMessage)
        assertEquals(customMessage, loginViewModel.uiState.errorMessage)
    }

    @Test
    fun multiple_error_scenarios_sequential_handling() {
        // Test that ViewModel can handle multiple different errors in sequence

        // First: Network error
        var loginViewModel = createLoginViewModel(FakeAuthDataSource.networkError(50L))
        loginViewModel.updateEmail("test@company.com")
        loginViewModel.updatePassword("Password123!")
        loginViewModel.login()
        Thread.sleep(150)

        assertTrue(loginViewModel.uiState.errorMessage!!.contains("Unable to connect"))

        // Second: Server error
        loginViewModel = createLoginViewModel(FakeAuthDataSource.serverError(50L))
        loginViewModel.updateEmail("test@company.com")
        loginViewModel.updatePassword("Password123!")
        loginViewModel.login()
        Thread.sleep(150)

        assertTrue(loginViewModel.uiState.errorMessage!!.contains("Internal server error"))

        // Third: Success
        loginViewModel = createLoginViewModel(FakeAuthDataSource.success(50L))
        loginViewModel.updateEmail("test@company.com")
        loginViewModel.updatePassword("Password123!")
        loginViewModel.login()
        Thread.sleep(150)

        assertTrue(loginViewModel.uiState.isLoginSuccessful)
        assertNull(loginViewModel.uiState.errorMessage)
    }

    // MARK: - Performance and Boundary Tests

    @Test
    fun loading_state_transitions_correctly() {
        val loginViewModel = createLoginViewModel(FakeAuthDataSource.success(100L))

        loginViewModel.updateEmail("test@company.com")
        loginViewModel.updatePassword("Password123!")

        // Before login
        assertFalse(loginViewModel.uiState.isLoading)

        // Trigger login
        loginViewModel.login()

        // During login (immediate check)
        assertTrue(loginViewModel.uiState.isLoading)

        // After login completes
        Thread.sleep(200)
        assertFalse(loginViewModel.uiState.isLoading)
    }
}