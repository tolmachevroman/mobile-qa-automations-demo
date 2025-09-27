package com.demo.automation.qa.di

import com.demo.automation.qa.data.repository.AuthRepositoryImpl
import com.demo.automation.qa.data.source.AuthDataSource
import com.demo.automation.qa.data.source.RemoteAuthDataSource
import com.demo.automation.qa.domain.repository.AuthRepository
import com.demo.automation.qa.domain.usecase.LoginUseCase
import com.demo.automation.qa.ui.auth.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Data Sources
    single<AuthDataSource> { RemoteAuthDataSource() }

    // Repositories
    single<AuthRepository> { AuthRepositoryImpl(get()) }

    // Use Cases
    single { LoginUseCase(get()) }

    // ViewModels
    viewModel { LoginViewModel(get()) }
}