package com.demo.automation.qa.data.repository

import com.demo.automation.qa.data.source.AuthDataSource
import com.demo.automation.qa.domain.model.AuthResult
import com.demo.automation.qa.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val remoteDataSource: AuthDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthResult {
        return try {
            remoteDataSource.authenticate(email, password)
        } catch (e: Exception) {
            AuthResult.Error("An unexpected error occurred: ${e.message}")
        }
    }
}