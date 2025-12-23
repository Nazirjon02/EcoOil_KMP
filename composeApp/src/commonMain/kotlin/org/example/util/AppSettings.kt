package org.example.util

import com.russhwolf.settings.Settings
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.data.TransactionDto
import org.example.data.TransactionsCache

object AppSettings {

    private val settings: Settings by lazy {
        try {
            Settings()
        } catch (e: Throwable) {
            // Заглушка для @Preview и случаев, когда Settings() не может создаться
            object : Settings {
                private val map = mutableMapOf<String, Any>()

                override val keys: Set<String> get() = map.keys
                override val size: Int get() = map.size

                override fun clear() = map.clear()
                override fun remove(key: String) {
                    TODO("Not yet implemented")
                }

                override fun hasKey(key: String): Boolean = map.containsKey(key)

                override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
                    map[key] as? Boolean ?: defaultValue

                override fun getDouble(key: String, defaultValue: Double): Double =
                    map[key] as? Double ?: defaultValue

                override fun getFloat(key: String, defaultValue: Float): Float =
                    map[key] as? Float ?: defaultValue

                override fun getInt(key: String, defaultValue: Int): Int =
                    map[key] as? Int ?: defaultValue

                override fun getLong(key: String, defaultValue: Long): Long =
                    map[key] as? Long ?: defaultValue

                override fun getString(key: String, defaultValue: String): String =
                    map[key] as? String ?: defaultValue

                override fun putBoolean(key: String, value: Boolean) { map[key] = value }
                override fun putDouble(key: String, value: Double) { map[key] = value }
                override fun putFloat(key: String, value: Float) { map[key] = value }
                override fun putInt(key: String, value: Int) { map[key] = value }
                override fun putLong(key: String, value: Long) { map[key] = value }
                override fun putString(key: String, value: String) { map[key] = value }

                override fun getBooleanOrNull(key: String): Boolean? = map[key] as? Boolean
                override fun getDoubleOrNull(key: String): Double? = map[key] as? Double
                override fun getFloatOrNull(key: String): Float? = map[key] as? Float
                override fun getIntOrNull(key: String): Int? = map[key] as? Int
                override fun getLongOrNull(key: String): Long? = map[key] as? Long
                override fun getStringOrNull(key: String): String? = map[key] as? String
            }
        }
    }

    // Обычные методы
    fun putString(key: String, value: String) = settings.putString(key, value)
    fun getString(key: String, default: String = ""): String = settings.getString(key, default)

    fun putInt(key: String, value: Int) = settings.putInt(key, value)
    fun getInt(key: String, default: Int = 0): Int = settings.getInt(key, default)

    fun putBoolean(key: String, value: Boolean) = settings.putBoolean(key, value)
    fun getBoolean(key: String, default: Boolean = false): Boolean = settings.getBoolean(key, default)

    fun putDouble(key: String, value: Double) = settings.putDouble(key, value)
    fun getDouble(key: String, default: Double = 0.0): Double = settings.getDouble(key, default)

    fun putLong(key: String, value: Long) = settings.putLong(key, value)
    fun getLong(key: String, value: Long) = settings.getLong(key, value)


    // clear и remove — просто прокидываем (в реальном Settings они suspend, но мы вызываем в suspend контексте если нужно)
    fun clear() = settings.clear()

    // Если где-то вызываешь remove синхронно — можно оставить как есть, но лучше сделать suspend
    fun remove(key: String) = settings.remove(key) // если ошибка — сделай suspend fun remove(key: String)

    // Безопасные версии для UI и Preview
    fun getStringSafe(key: String, default: String = ""): String = try {
        getString(key, default)
    } catch (e: Throwable) {
        default
    }

    fun getIntSafe(key: String, default: Int = 0): Int = try {
        getInt(key, default)
    } catch (e: Throwable) {
        default
    }

    fun getBooleanSafe(key: String, default: Boolean = false): Boolean = try {
        getBoolean(key, default)
    } catch (e: Throwable) {
        default
    }

    fun getDoubleSafe(key: String, default: Double = 0.0): Double = try {
        getDouble(key, default)
    } catch (e: Throwable) {
        default
    }

    private const val KEY_TX_CACHE = "tx_cache_v1"

    private val JsonRelaxed = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun saveTransactionsToCache(items: List<TransactionDto>) {
        val json = JsonRelaxed.encodeToString(TransactionsCache(items))
        AppSettings.putString(KEY_TX_CACHE, json)
    }

    fun loadTransactionsFromCache(): List<TransactionDto> {
        val json = AppSettings.getString(KEY_TX_CACHE, "")
        if (json.isBlank()) return emptyList()
        return try {
            JsonRelaxed.decodeFromString<TransactionsCache>(json).items
        } catch (_: Throwable) {
            emptyList()
        }
    }
}

