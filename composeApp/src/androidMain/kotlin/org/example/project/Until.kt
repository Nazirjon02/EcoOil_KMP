package org.example.project

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.widget.Toast
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


// ⚠️ Нужен контекст приложения, поэтому сделаем init(...)
actual object ToastManager {

    private lateinit var appContext: Context

    /**
     * Инициализация менеджера тостов.
     * Вызываем один раз из Application или Activity.
     */
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    actual fun show(message: String) {
        if (!this::appContext.isInitialized) {
            // На всякий случай, чтобы не упасть, если забыли init
            return
        }
        Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show()
    }
}