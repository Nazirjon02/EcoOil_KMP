package org.example.project.until

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun SnackbarHostWrapper(
    snackbarMessage: String?,
    onSnackbarDismissed: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Логика для показа Snackbar
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            onSnackbarDismissed() // вызываем коллбек, чтобы сбросить сообщение
        }
    }

    // Создаем структуру, которая показывает Snackbar
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        // Место для вашего контента
        Column(modifier = Modifier.padding(paddingValues)) {
            // Ваш основной UI
        }
    }
}
