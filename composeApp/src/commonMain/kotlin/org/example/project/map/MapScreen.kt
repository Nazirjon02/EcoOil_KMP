package org.example.project.map

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.data.Location
import org.example.data.MapStation
import org.example.data.SelectedStationState

@Composable
fun MapScreen(
    stations: List<MapStation>,
    userLocation: Location? = null
) {
    val selectedStationState = remember { SelectedStationState() }

    MapsLayout(
        stations = stations,
        selectedStationState = selectedStationState,
        userLocation = userLocation
    )

    selectedStationState.station?.let { station ->
        if (selectedStationState.showDialog) {
            AlertDialog(
                onDismissRequest = { selectedStationState.showDialog = false },
                title = { Text(station.name) },
                text = {
                    Column {
                        Text("Адрес: ${station.snippet.ifBlank { "Не указан" }}")

                        Spacer(Modifier.height(8.dp))
                        Text("Услуги:", fontWeight = FontWeight.Bold)
                        Text("Магазин: ${if (station.hasShop) "Да" else "Нет"}")
                        Text("Круглосуточно: ${if (station.workAroundTime) "Да" else "Нет"}")
                        Text("Кофе: ${if (station.hasCoffee) "Да" else "Нет"}")
                        Text("Туалет: ${if (station.hasToilet) "Да" else "Нет"}")
                        Text("Терминал оплаты: ${if (station.hasPayTerminal) "Да" else "Нет"}")

                        Spacer(Modifier.height(12.dp))
                        Text("Цены на топливо:", fontWeight = FontWeight.Bold)

                        if (station.prices.isEmpty()) {
                            Text("Нет данных")
                        } else {
                            station.prices.forEach { p ->
                                Text("${p.label}: ${p.price} ${p.currency}")
                            }
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            val to = Location(station.latitude, station.longitude)
                            RouteNavigator.openRoute(
                                from = userLocation, // если null -> откроется просто точка
                                to = to,
                                title = station.name
                            )
                        }
                    ) { Text("Маршрут") }
                },
                confirmButton = {
                    TextButton(onClick = { selectedStationState.showDialog = false }) {
                        Text("Закрыть")
                    }
                }
            )
        }
    }
}