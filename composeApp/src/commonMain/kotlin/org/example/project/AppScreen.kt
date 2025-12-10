package org.example.project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import org.example.networking.InsultCensorClient
import org.example.networking.createHttpClient

object AppScreen : Screen {

    @Composable
    override fun Content() {
        val client = remember {
            InsultCensorClient(
                createHttpClient(PlatformHttpEngine())
            )
        }

        AppContent(client)
    }
}
