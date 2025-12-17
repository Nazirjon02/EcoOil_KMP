package org.example.project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import org.example.networking.Constant
import org.example.networking.InsultCensorClient
import org.example.networking.createHttpClient
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.LocalNavigator
import org.example.util.AppSettings

object AuthScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        // Читаем токен
        val token = remember { AppSettings.getString("token") }

        // Если токен есть, сразу переходим на MainScreen
        LaunchedEffect(token) {
            if (token.isNotEmpty()) {
                navigator.replace(MainRootScreen) // заменяем экран, чтобы AuthScreen не оставался в стеке
            }
        }

        // Если токена нет, показываем AuthScreen
        if (token.isEmpty()) {
            val client = remember {
                InsultCensorClient(
                    createHttpClient(PlatformHttpEngine()),
                    baseUrl = Constant.baseUrl
                )
            }
            AppContent(client)
        }
    }
}

