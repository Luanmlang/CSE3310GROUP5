package com.example.loginandprofile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.loginandprofile.ui.theme.LoginAndProfileTheme

// Which screen is currently visible
sealed class Screen {
    object Login    : Screen()
    object Register : Screen()
    object Profile  : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginAndProfileTheme {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    // In-memory user store – replace with a real database/ViewModel later
    val userStore = remember { mutableStateMapOf<String, UserData>() }
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
    var loggedInUsername by remember { mutableStateOf("") }

    when (currentScreen) {
        is Screen.Login -> {
            LoginScreen(
                userStore = userStore,
                onLoginSuccess = { username ->
                    loggedInUsername = username
                    currentScreen = Screen.Profile
                },
                onRegisterClick = {
                    currentScreen = Screen.Register
                }
            )
        }
        is Screen.Register -> {
            RegisterScreen(
                onRegisterSuccess = { userData ->
                    userStore[userData.username] = userData
                    currentScreen = Screen.Login
                },
                onBackClick = {
                    currentScreen = Screen.Login
                }
            )
        }
        is Screen.Profile -> {
            ProfileScreen(
                userData = userStore[loggedInUsername] ?: UserData(loggedInUsername, "", ""),
                onSave = { updated ->
                    // Remove old key in case username changed
                    userStore.remove(loggedInUsername)
                    userStore[updated.username] = updated
                    loggedInUsername = updated.username
                },
                onLogout = {
                    loggedInUsername = ""
                    currentScreen = Screen.Login
                }
            )
        }
    }
}

// ── Shared data model ────────────────────────────────────────────────────────
data class UserData(
    val username: String,
    val password: String,
    val email: String,
    val profileImageUri: String = ""
)