package org.example.project

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import java.security.MessageDigest

lateinit var appContext: Context   // Установишь в Application

actual object Until {

    actual fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray())

        return bytes.joinToString("") { "%02x".format(it) }
    }

    @SuppressLint("HardwareIds")
    actual fun getDeviceId(): String {
        return Settings.Secure.getString(
            appContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }
}
