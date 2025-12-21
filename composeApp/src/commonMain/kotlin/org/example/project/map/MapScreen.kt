package org.example.project.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
            StationDialog(
                station = station,
                userLocation = userLocation,
                onClose = { selectedStationState.showDialog = false }
            )
        }
    }
}

@Composable
fun StationDialog(
    station: MapStation,
    userLocation: Location?,
    onClose: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Column {
                Text(
                    text = station.name,
                    style = MaterialTheme.typography.titleLarge
                )
                if (station.snippet.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = station.snippet,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {

                // --- УСЛУГИ ---
                Text(
                    text = "Услуги",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ServiceChip("Магазин", station.hasShop)
                    ServiceChip("24/7", station.workAroundTime)
                    ServiceChip("Кофе", station.hasCoffee)
                    ServiceChip("Туалет", station.hasToilet)
                    ServiceChip("Терминал", station.hasPayTerminal)
                }

                Spacer(Modifier.height(16.dp))

                // --- ЦЕНЫ ---
                Text(
                    text = "Топливо",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))

                if (station.prices.isEmpty()) {
                    Text(
                        text = "Нет данных",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    station.prices.forEach { p ->
                        PriceRow(
                            label = p.label,
                            price = "${p.price} ${p.currency}"
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    RouteNavigator.openRoute(
                        from = userLocation,
                        to = Location(station.latitude, station.longitude),
                        title = station.name
                    )
                }
            ) {
                Text("Маршрут")
            }
        },
        dismissButton = {
            TextButton(onClick = onClose) {
                Text("Закрыть")
            }
        }
    )
}



@Composable
fun ServiceChip(label: String, enabled: Boolean) {
    AssistChip(
        onClick = {},
        label = { Text(label) },
        enabled = enabled,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (enabled)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    )
}

@Composable
fun PriceRow(label: String, price: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        Text(
            text = price,
            fontWeight = FontWeight.Medium
        )
    }
}