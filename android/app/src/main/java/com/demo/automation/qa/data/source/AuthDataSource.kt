package com.demo.automation.qa.data.source

import com.demo.automation.qa.domain.model.AuthResult

interface AuthDataSource {
    suspend fun authenticate(email: String, password: String): AuthResult
}