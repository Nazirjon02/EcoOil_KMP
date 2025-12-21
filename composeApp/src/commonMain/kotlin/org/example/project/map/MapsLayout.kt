package org.example.project.map

import androidx.compose.runtime.Composable
import org.example.data.Location
import org.example.data.MapStation
import org.example.data.SelectedStationState



@Composable
expect fun MapsLayout(
    stations: List<MapStation>,
    selectedStationState: SelectedStationState,
    onMapReady: () -> Unit = {}, // если нужно что-то после загрузки
    userLocation: Location? = null // текущая геопозиция пользователя
)

expect object RouteNavigator {
    fun openRoute(from: Location?, to: Location, title: String? = null)
}