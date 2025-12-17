package org.example.util

import com.russhwolf.settings.Settings

object AppSettings {
    private val settings = Settings()

    // Сохранить строку по ключу
    fun putString(key: String, value: String) {
        settings.putString(key, value)
    }

    // Прочитать строку по ключу, дефолтное значение = ""
    fun getString(key: String, default: String = ""): String {
        return settings.getString(key, default)
    }

    // Сохранить Int
    fun putInt(key: String, value: Int) {
        settings.putInt(key, value)
    }

    // Прочитать Int
    fun getInt(key: String, default: Int = 0): Int {
        return settings.getInt(key, default)
    }

    // Сохранить Boolean
    fun putBoolean(key: String, value: Boolean) {
        settings.putBoolean(key, value)
    }

    // Прочитать Boolean
    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return settings.getBoolean(key, default)
    }

    // Сохранить Double
    fun putDouble(key: String, value: Double) {
        settings.putDouble(key, value)
    }

    fun getDouble(key: String, default: Double = 0.0): Double {
        return settings.getDouble(key, default)
    }

    // Удалить ключ
    fun remove(key: String) {
        settings.remove(key)
    }

    // Очистить все
    fun clear() {
        settings.clear()
    }
}
