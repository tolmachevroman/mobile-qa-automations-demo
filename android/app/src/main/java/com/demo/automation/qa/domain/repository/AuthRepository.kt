package com.demo.automation.qa.domain.repository

import com.demo.automation.qa.domain.model.AuthResult

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResult
}