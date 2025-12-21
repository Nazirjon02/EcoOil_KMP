package org.example.project.map

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import org.example.data.Location
import org.example.data.MapStation
import org.example.data.SelectedStationState
import androidx.core.net.toUri
import org.example.project.appContext

private val KHUJAND = LatLng(40.2833, 69.6167)
private const val ZOOM_CITY = 12f
private const val ZOOM_ME = 15f

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun MapsLayout(
    stations: List<MapStation>,
    selectedStationState: SelectedStationState,
    onMapReady: () -> Unit,
    userLocation: Location?
) {
    val permissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    // Центр: пользователь если есть, иначе Худжанд
    val target = userLocation?.let { LatLng(it.latitude, it.longitude) } ?: KHUJAND
    val zoom = if (userLocation != null) ZOOM_ME else ZOOM_CITY

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(target, zoom)
    }

    // Флаг "карта загружена"
    var mapLoaded by remember { mutableStateOf(false) }

    // Меняем позицию только после загрузки карты (без CameraUpdateFactory)
    LaunchedEffect(mapLoaded, userLocation?.latitude, userLocation?.longitude) {
        if (!mapLoaded) return@LaunchedEffect

        val t = userLocation?.let { LatLng(it.latitude, it.longitude) } ?: KHUJAND
        val z = if (userLocation != null) ZOOM_ME else ZOOM_CITY

        cameraPositionState.position = CameraPosition.fromLatLngZoom(t, z)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = permissionsState.allPermissionsGranted
        ),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = true,
            zoomControlsEnabled = false
        ),
        onMapLoaded = {
            mapLoaded = true
            onMapReady()
        }
    ) {
        stations.forEach { station ->
            val position = LatLng(station.latitude, station.longitude)
            Marker(
                state = rememberMarkerState(position = position),
                title = station.name,
                snippet = station.snippet,
                onClick = {
                    selectedStationState.station = station
                    selectedStationState.showDialog = true
                    true
                }
            )
        }
    }

    // Запрос разрешений (как у тебя, но исправлено Text)
    if (!permissionsState.allPermissionsGranted) {
        if (permissionsState.shouldShowRationale) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Нужна геолокация") },
                text = { Text("Чтобы показать ваше местоположение на карте и ближайшие АЗС, разрешите доступ к геолокации.") },
                confirmButton = {
                    TextButton(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
                        Text("Разрешить")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { }) { Text("Отказать") }
                }
            )
        } else {
            LaunchedEffect(Unit) {
                permissionsState.launchMultiplePermissionRequest()
            }
        }
    }
}


actual object RouteNavigator {
    actual fun openRoute(from: Location?, to: Location, title: String?) {
        val uri = if (from != null) {
            ("https://www.google.com/maps/dir/?api=1" +
                    "&origin=${from.latitude},${from.longitude}" +
                    "&destination=${to.latitude},${to.longitude}").toUri()
        } else {
            Uri.parse("geo:${to.latitude},${to.longitude}?q=${to.latitude},${to.longitude}(${Uri.encode(title ?: "Точка")})")
        }

        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // Вариант 1: если у тебя есть глобальный appContext
        appContext.startActivity(intent)
    }
}