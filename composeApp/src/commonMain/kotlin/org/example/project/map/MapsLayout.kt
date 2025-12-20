package org.example.project.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.example.data.FuelPrice
import org.example.data.Location

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
    // остальные поля добавь по необходимости: workHours, phone, services и т.д.
)

class SelectedStationState {
    var station: Station? by mutableStateOf(null)
    var showDialog: Boolean by mutableStateOf(false)
}

@Composable
expect fun MapsLayout(
    stations: List<Station>,
    selectedStationState: SelectedStationState,
    onMapReady: () -> Unit = {}, // если нужно что-то после загрузки
    userLocation: Location? = null // текущая геопозиция пользователя
)