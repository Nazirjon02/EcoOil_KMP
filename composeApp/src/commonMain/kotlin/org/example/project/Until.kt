package org.example.project

import androidx.compose.ui.graphics.Color


expect object Until {
    fun sha256(input: String): String
    fun getDeviceId(): String


}

expect object ToastManager {
    fun show(message: String)
}


