package org.example.project.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState


import androidx.compose.runtime.*
import com.google.maps.android.compose.*
import org.example.data.Location

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import io.ktor.websocket.Frame

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun MapsLayout(
    stations: List<Station>,
    selectedStationState: SelectedStationState,
    onMapReady: () -> Unit,
    userLocation: Location?
) {
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val initialPosition = userLocation?.let {
        LatLng(it.latitude, it.longitude)
    } ?: LatLng(41.311081, 69.240562) // Ташкент по умолчанию

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 12f)
    }

    var mapProperties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = false)) }

    // Если разрешения уже есть — включаем геолокацию
    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.allPermissionsGranted) {
            mapProperties = mapProperties.copy(isMyLocationEnabled = true)
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = mapProperties,
        onMapLoaded = onMapReady
    ) {
        // Твои маркеры станций (как было раньше)
        stations.forEach { station ->
            val position = LatLng(station.latitude, station.longitude)
            Marker(
                state = rememberMarkerState(position = position),
                title = station.name,
                snippet = station.address ?: "Адрес не указан",
                onClick = {
                    selectedStationState.station = station
                    selectedStationState.showDialog = true
                    false
                }
            )
        }
    }

    // UI для запроса разрешения
    if (!locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.shouldShowRationale) {
            // Пользователь ранее отказал — объясни зачем нужно
            AlertDialog(
                onDismissRequest = { },
                title = { Frame.Text("Нужна геолокация") },
                text = { Frame.Text("Чтобы показать ваше местоположение на карте и ближайшие заправки, разрешите доступ к геолокации.") },
                confirmButton = {
                    TextButton(onClick = { locationPermissionsState.launchMultiplePermissionRequest() }) {
                        Text("Разрешить")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { /* можно закрыть или оставить карту без гео */ }) {
                        Text("Отказать")
                    }
                }
            )
        } else {
            // Первый запрос
            LaunchedEffect(Unit) {
                locationPermissionsState.launchMultiplePermissionRequest()
            }
        }
    }
}