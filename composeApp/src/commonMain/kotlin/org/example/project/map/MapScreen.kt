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
import cafe.adriel.voyager.core.screen.Screen
import org.example.data.ApiResponse
import org.example.data.FuelPrice
import org.example.data.Location
import org.example.data.StationsResponse

object MapScreen : Screen {
    @Composable
    override fun Content() {
        // Тестовые станции — вытащили из твоего testResponse
        val testStations = listOf(
            Station(
                id = 1,
                name = "EcoOil Ташкент",
                address = "ул. Амира Темура, 45",
                latitude = 41.311081,
                longitude = 69.240562,
                distance = 2.5,
                is_open = true,
                rating = 4.5f,
                fuel_prices = listOf(
                    FuelPrice("ai92", 11500.0),
                    FuelPrice("ai95", 12500.0),
                    FuelPrice("diesel", 11000.0)
                )
            ),
            Station(
                id = 2,
                name = "EcoOil Чиланзар",
                address = "ул. Чиланзар, 12",
                latitude = 41.288056,
                longitude = 69.204722,
                distance = 5.7,
                is_open = true,
                rating = 4.2f,
                fuel_prices = listOf(
                    FuelPrice("ai92", 11600.0),
                    FuelPrice("ai95", 12600.0)
                )
            ),
            Station(
                id = 3,
                name = "EcoOil Юнусабад",
                address = "ул. Шахрисабз, 23",
                latitude = 41.335000,
                longitude = 69.280000,
                distance = 8.2,
                is_open = false,
                rating = 4.8f,
                fuel_prices = listOf(
                    FuelPrice("ai92", 11400.0),
                    FuelPrice("ai95", 12400.0),
                    FuelPrice("diesel", 10900.0),
                    FuelPrice("gas", 8500.0)
                )
            )
        )

        MapScreen(stations = testStations) // передаём в наш composable
    }
}


@Composable
fun MapScreen(
    stations: List<Station>,
    userLocation: Location? = null
) {
    val selectedStationState = remember { SelectedStationState() }

    MapsLayout(
        stations = stations,
        selectedStationState = selectedStationState,
        userLocation = userLocation
    )

    // Диалог с информацией о станции
    selectedStationState.station?.let { station ->
        if (selectedStationState.showDialog) {
            AlertDialog(
                onDismissRequest = { selectedStationState.showDialog = false },
                title = { Text(station.name) },
                text = {
                    Column {
                        Text("Адрес: ${station.address ?: "Не указан"}")
                        Text("Статус: ${if (station.is_open) "Открыто" else "Закрыто"}")
                        Text("Рейтинг: ${station.rating}")
                        Text("Расстояние: ${station.distance?.formatOneDecimal() ?: "—"}")
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Цены на топливо:", fontWeight = FontWeight.Bold)
                        station.fuel_prices.forEach { price ->
                            Text("${price.fuel_type.uppercase()}: ${price.price} ${price.currency}")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { selectedStationState.showDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}


fun Double.formatOneDecimal(): String {
    val rounded = kotlin.math.round(this * 10) / 10
    return "$rounded км"
}