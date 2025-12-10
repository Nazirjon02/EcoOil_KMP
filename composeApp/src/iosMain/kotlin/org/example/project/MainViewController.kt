package org.example.project

import androidx.compose.ui.window.ComposeUIViewController
import cafe.adriel.voyager.navigator.Navigator

fun MainViewController() = ComposeUIViewController {
    Navigator(AuthScreen)
}
