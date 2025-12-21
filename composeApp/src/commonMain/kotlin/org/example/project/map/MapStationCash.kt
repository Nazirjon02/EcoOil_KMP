package org.example.project.map

import io.ktor.util.date.getTimeMillis
import org.example.util.AppSettings


import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.data.MapStation
import org.example.networking.InsultCensorClient

@Serializable
data class MapStationsCache(
    val savedAt: Long,
    val stations: List<MapStation>
)

object MapCacheStore {
    private const val KEY_CACHE_JSON = "map_cache_json"
    private const val KEY_CACHE_TS = "map_cache_ts"

    // TTL кэша: например 6 часов
    private const val TTL_MS: Long = 6L * 60L * 60L * 1000L

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun save(stations: List<MapStation>) {
        val now = nowMs()
        val payload = MapStationsCache(savedAt = now, stations = stations)
        val raw = json.encodeToString(payload)

        AppSettings.putString(KEY_CACHE_JSON, raw)
        AppSettings.putLong(KEY_CACHE_TS, now)
    }

    fun loadIfFresh(): List<MapStation>? {
        val raw = AppSettings.getString(KEY_CACHE_JSON)
        if (raw.isBlank()) return null

        val savedAt = AppSettings.getLong(KEY_CACHE_TS,TTL_MS)
        val now = nowMs()
        val isFresh = savedAt > 0 && (now - savedAt) <= TTL_MS
        if (!isFresh) return null

        return try {
            val payload = json.decodeFromString<MapStationsCache>(raw)
            payload.stations
        } catch (_: Throwable) {
            null
        }
    }

    fun loadEvenIfStale(): List<MapStation>? {
        val raw = AppSettings.getString(KEY_CACHE_JSON)
        if (raw.isBlank()) return null
        return try {
            val payload = json.decodeFromString<MapStationsCache>(raw)
            payload.stations
        } catch (_: Throwable) {
            null
        }
    }

    fun clear() {
        AppSettings.putString(KEY_CACHE_JSON, "")
        AppSettings.putLong(KEY_CACHE_TS, 0L)
    }

    private fun nowMs(): Long = getTimeMillis()
}



/**
 * Сначала пытаемся взять свежий кэш.
 * Если кэша нет/просрочен — грузим с сервера и сохраняем.
 *
 * @param forceRefresh true -> всегда идём на сервер
 * @param allowStaleIfOffline если сервер упал, но есть старый кэш — возвращаем его
 */
suspend fun getStationsMapCached(
    client: InsultCensorClient?,
    forceRefresh: Boolean = false,
    allowStaleIfOffline: Boolean = true,
    onSuccess: (List<MapStation>) -> Unit,
    onError: (Throwable?) -> Unit
) {
    try {
        if (!forceRefresh) {
            MapCacheStore.loadIfFresh()?.let { cached ->
                onSuccess(cached)
                return
            }
        }

        // Если не нашли свежий кэш — идём в сеть твоей функцией
        getStationsMap(
            client = client,
            onSuccess = { stations ->
                // сохраняем в кэш
                MapCacheStore.save(stations)
                onSuccess(stations)
            },
            onError = { e ->
                // если сеть упала, но есть старый кэш — вернём его (офлайн режим)
                if (allowStaleIfOffline) {
                    val stale = MapCacheStore.loadEvenIfStale()
                    if (!stale.isNullOrEmpty()) {
                        onSuccess(stale)
                        return@getStationsMap
                    }
                }
                onError(e)
            }
        )
    } catch (e: Throwable) {
        onError(e)
    }
}