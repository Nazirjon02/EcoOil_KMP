package org.example.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.serialization.Serializable
@Serializable
data class ResponseMap(
    val code: Int,
    val message: String,
    val data: Data
)
@Serializable
data class Data(
    val station_count: Int,
    val list_station_map: List<Station>
)
@Serializable
data class Station(
    val station_name: String,
    val station_snippet: String,
    val station_latitude: String,
    val station_longitude: String,
    val station_shop: Int,
    val station_work_around_time: Int,
    val station_coffee: Int,
    val station_toilet: Int,
    val station_pay_terminal: Int,
    val ai95_price: String?,
    val ai92_price: String?,
    val dt_price: String?,
    val gas_price: String?,
    val dtecto_price: String?
)
@Serializable
data class Location(
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class MapFuelPrice(
    val key: String,          // "ai95", "ai92", ...
    val label: String,        // "АИ-95", ...
    val price: String,        // строкой, как в API (или форматированная)
    val currency: String = "TJS"
)

@Serializable
data class MapStation(
    val name: String,
    val snippet: String,
    val latitude: Double,
    val longitude: Double,

    val hasShop: Boolean,
    val workAroundTime: Boolean,
    val hasCoffee: Boolean,
    val hasToilet: Boolean,
    val hasPayTerminal: Boolean,

    val prices: List<MapFuelPrice>
)

class SelectedStationState {
    var station: MapStation? by mutableStateOf(null)
    var showDialog: Boolean by mutableStateOf(false)
}