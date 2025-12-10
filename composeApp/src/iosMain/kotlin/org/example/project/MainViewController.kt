package org.example.project

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import cafe.adriel.voyager.navigator.Navigator
import io.ktor.client.engine.darwin.Darwin
import org.example.networking.InsultCensorClient
import org.example.networking.createHttpClient

fun MainViewController() = ComposeUIViewController {
    Navigator(AppScreen)
}
