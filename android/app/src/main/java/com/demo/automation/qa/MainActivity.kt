package com.demo.automation.qa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.demo.automation.qa.ui.auth.LoginScreen
import com.demo.automation.qa.ui.dashboard.DashboardScreen
import com.demo.automation.qa.ui.theme.QAAutomationDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QAAutomationDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QADemoApp()
                }
            }
        }
    }
}

@Composable
fun QADemoApp() {
    var currentScreen by remember { mutableStateOf("login") }
    var userEmail by remember { mutableStateOf("") }

    when (currentScreen) {
        "login" -> {
            LoginScreen(
                onLoginSuccess = { email ->
                    userEmail = email
                    currentScreen = "dashboard"
                }
            )
        }

        "dashboard" -> {
            DashboardScreen(
                userEmail = userEmail
            )
        }
    }
}