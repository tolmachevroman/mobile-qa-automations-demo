# Mobile QA Automation Demo - Android

This project demonstrates a comprehensive approach to mobile QA automation where **YAML behavior
definitions drive both implementation and testing**.

## 📋 Project Overview

This demo showcases:

1. **YAML-driven behavior definition** for user stories and acceptance criteria
2. **Clean architecture implementation** with proper separation of concerns
3. **Comprehensive UI automation testing** based on YAML specifications
4. **Modern Android development** with Jetpack Compose, Coroutines, and MVVM

## 🏗️ Architecture

### Layer Structure

```
├── Domain Layer
│   ├── models/          # User, AuthResult, AuthException
│   ├── repository/      # AuthRepository (interface)
│   └── usecase/         # LoginUseCase
├── Data Layer
│   ├── repository/      # AuthRepositoryImpl
│   └── source/          # RemoteAuthDataSource (mock)
├── UI Layer
│   ├── auth/           # LoginScreen, LoginViewModel
│   ├── dashboard/      # DashboardScreen
│   └── theme/          # Material Design 3 theme
└── DI Layer
    └── di/             # Dependency injection setup
```

## 📄 YAML Behavior Definition

The project uses [`bdd/user_login.yaml`](bdd/user_login.yaml) to define:

- **User stories** and acceptance criteria
- **Test scenarios** with priorities and steps
- **UI element requirements** with test tags
- **Business rules** and validation logic
- **Data contracts** for requests/responses

### Key Test Scenarios Defined

1. **successful_login** (Critical) - Valid credentials navigation
2. **invalid_email_format** (High) - Email validation errors
3. **empty_credentials** (High) - Required field validation
4. **password_visibility_toggle** (Medium) - UI interaction testing

## 🧪 Testing Strategy

### Modern DI-Based Testing

The project includes comprehensive tests in [
`LoginScreenTest.kt`](app/src/androidTest/java/com/demo/automation/qa/LoginScreenTest.kt):

- **Proper dependency injection** - Uses Koin for production, clean manual DI for tests
- **Fake implementations** - `FakeAuthDataSource` for deterministic testing
- **Architecture verification** - Ensures all layers work together
- **Business logic testing** - Email/password validation rules
- **State management** - ViewModel state transitions
- **Error scenario testing** - Network errors, invalid credentials
- **UI interaction** - Password visibility toggle
- **Real-world API scenarios** - HTTP status codes (401, 500, 503, 429)
- **Edge cases** - Malformed responses, timeouts, maintenance mode
- **Custom error handling** - Unknown fields in JSON responses

### Comprehensive Test Coverage

```kotlin
// HTTP Status Code Scenarios
@Test fun unauthorized_401_error_handling() { ... }
@Test fun server_error_500_handling() { ... }
@Test fun service_unavailable_503_handling() { ... }
@Test fun rate_limited_429_handling() { ... }

// Network and System Issues
@Test fun network_error_simulation() { ... }
@Test fun malformed_response_handling() { ... }
@Test fun maintenance_mode_handling() { ... }

// Edge Cases
@Test fun custom_error_message_handling() { ... }
@Test fun multiple_error_scenarios_sequential_handling() { ... }
@Test fun loading_state_transitions_correctly() { ... }
```

### Fake vs Real Implementations

```kotlin
// Production: Real remote data source
RemoteAuthDataSource() // Random delays, real network simulation

// Testing: Fake data source with controlled scenarios
FakeAuthDataSource.success(50L)           // Happy path
FakeAuthDataSource.networkError(50L)      // Network issues
FakeAuthDataSource.serverError(50L)       // 500 errors
FakeAuthDataSource.unauthorized(50L)      // 401 errors
FakeAuthDataSource.rateLimited(50L)       // 429 rate limiting
FakeAuthDataSource.malformedResponse(50L) // Invalid JSON
```

### Test Results

✅ **17/17 tests passing** - Complete coverage of real-world scenarios

## 🚀 Features Implemented

### Login Screen (`LoginScreen.kt`)

- Email input with real-time validation
- Password input with visibility toggle
- Form validation with error messages
- Loading states during authentication
- Material Design 3 UI components
- Comprehensive test tags for automation

### Business Logic (`LoginViewModel.kt`)

- Real-time field validation
- Form state management
- Authentication flow handling
- Error state management
- Clean separation from UI

### Data Layer

- Mock authentication service
- Repository pattern implementation
- Proper error handling
- Coroutine-based async operations

## 📱 UI Components with Test Tags

All UI components include test tags matching the YAML specification:

- `login_email_field` - Email input field
- `login_password_field` - Password input field
- `login_submit_button` - Login button
- `password_visibility_toggle` - Password show/hide toggle
- `login_error_message` - Error message display
- `login_loading_indicator` - Loading progress indicator

## 🔧 Technology Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material Design 3
- **Architecture**: Clean Architecture + MVVM
- **Async**: Coroutines + Flow
- **Serialization**: kotlinx.serialization
- **DI**: Koin
- **Testing**: JUnit4, Espresso, Compose Test
- **Build**: Gradle with Version Catalogs

## 📊 YAML-to-Implementation Mapping

| YAML Section | Implementation | Test Coverage |
|--------------|----------------|---------------|
| `acceptance_criteria` | LoginViewModel validation | ✅ Business logic tests |
| `test_scenarios` | LoginScreenTest methods | ✅ Scenario-based tests |  
| `ui_elements.testTag` | Compose testTag modifiers | ✅ UI element verification |
| `business_rules` | Domain layer validation | ✅ Validation rule tests |
| `data_requirements` | Domain models | ✅ Architecture tests |

## 🏃‍♂️ Running the Project

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK API 24+
- Android device/emulator with API 36

### Build and Test

```bash
# Build the project
./gradlew build

# Run instrumented tests
./gradlew connectedDebugAndroidTest

# Install and run on device
./gradlew installDebug
```

### Demo Credentials

- Email: `test@company.com`
- Password: `Password123!`

## 🎯 Key Benefits Demonstrated

1. **Traceability**: Every UI element and test maps back to YAML requirements
2. **Maintainability**: YAML changes drive implementation updates
3. **Quality**: Comprehensive test coverage based on acceptance criteria
4. **Collaboration**: Product owners can define behavior in readable YAML
5. **Automation**: Tests verify exact behavior specified in requirements
6. **Modern DI**: Proper dependency injection with Koin for production
7. **Testability**: Fake implementations enable deterministic, fast testing
8. **Architecture**: Clean separation with no factory anti-patterns

## 🔮 Future Enhancements

- [ ] Implement full Compose UI testing
- [ ] Add network layer with Retrofit
- [ ] Create YAML parser to generate test cases automatically
- [ ] Add accessibility testing
- [ ] Implement screenshot testing

## 📝 Notes

This demo proves that YAML-driven development can create a seamless flow from requirements →
implementation → testing, ensuring complete alignment between what's specified, built, and tested.