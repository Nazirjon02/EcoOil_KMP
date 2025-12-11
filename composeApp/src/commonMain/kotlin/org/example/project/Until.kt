package org.example.project


expect object Until {
    fun sha256(input: String): String
    fun getDeviceId(): String


}

expect object ToastManager {
    fun show(message: String)
}