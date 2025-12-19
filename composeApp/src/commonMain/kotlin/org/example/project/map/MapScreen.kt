package org.example.project.map

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

object MapScreen : Screen {
    @Composable
    override fun Content() {
        MapsLayout()
    }
}
