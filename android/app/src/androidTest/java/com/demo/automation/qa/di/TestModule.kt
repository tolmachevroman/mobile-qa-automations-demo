package com.demo.automation.qa.di

import com.demo.automation.qa.data.repository.AuthRepositoryImpl
import com.demo.automation.qa.data.source.AuthDataSource
import com.demo.automation.qa.domain.repository.AuthRepository
import com.demo.automation.qa.domain.usecase.LoginUseCase
import com.demo.automation.qa.fake.FakeAuthDataSource
import com.demo.automation.qa.ui.auth.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val testModule = module {

    // Fake Data Sources for testing - default success scenario
    factory<AuthDataSource> {
        FakeAuthDataSource.success(networkDelay = 50L)
    }

    // Real repositories (they just delegate to data sources)
    factory<AuthRepository> { AuthRepositoryImpl(get()) }

    // Real use cases
    factory { LoginUseCase(get()) }

    // ViewModels
    viewModel { LoginViewModel(get()) }
}

// Module for testing network errors
val networkErrorTestModule = module {
    factory<AuthDataSource> {
        FakeAuthDataSource.networkError(networkDelay = 50L)
    }
    factory<AuthRepository> { AuthRepositoryImpl(get()) }
    factory { LoginUseCase(get()) }
    viewModel { LoginViewModel(get()) }
}

// Module for testing invalid credentials
val invalidCredentialsTestModule = module {
    factory<AuthDataSource> {
        FakeAuthDataSource.invalidCredentials(networkDelay = 50L)
    }
    factory<AuthRepository> { AuthRepositoryImpl(get()) }
    factory { LoginUseCase(get()) }
    viewModel { LoginViewModel(get()) }
}

// Module for testing server errors
val serverErrorTestModule = module {
    factory<AuthDataSource> {
        FakeAuthDataSource.serverError(networkDelay = 50L)
    }
    factory<AuthRepository> { AuthRepositoryImpl(get()) }
    factory { LoginUseCase(get()) }
    viewModel { LoginViewModel(get()) }
}