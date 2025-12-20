package org.example.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T
)

@Serializable
data class StationsResponse(
    val station_count: Int,
    val list_station_map: List<Station>
)

@Serializable
data class Station(
    val id: Long,
    val name: String,
    val address: String? = null,
    val latitude: Double,
    val longitude: Double,
    val distance: Double? = null,
    val is_open: Boolean = true,
    val rating: Float = 0f,
    val fuel_prices: List<FuelPrice> = emptyList()
)

@Serializable
data class FuelPrice(
    val fuel_type: String,
    val price: Double,
    val currency: String = "TJS"
)

// Общий класс для координат
@Serializable
data class Location(
    val latitude: Double,
    val longitude: Double
)

// Состояние для выбранной станции
class SelectedStationState {
    var station: Station? by mutableStateOf(null)
    var showDialog by mutableStateOf(false)
}