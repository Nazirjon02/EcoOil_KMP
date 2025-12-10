package org.example.project

import cafe.adriel.voyager.core.screen.Screen
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text

object HomeScreen : Screen {
    @Composable
    override fun Content() {
        Text("Home Screen")
    }
}
