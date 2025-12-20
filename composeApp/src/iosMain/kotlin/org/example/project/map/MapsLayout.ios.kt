package org.example.project.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation
import kotlinx.cinterop.*
import org.example.data.Location
import platform.CoreLocation.*
import platform.MapKit.*
import platform.Foundation.*
import platform.darwin.NSObject
// iosMain — файл MapsLayout.kt

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.*
import platform.CoreLocation.*
import platform.MapKit.*
import platform.Foundation.*
import platform.darwin.*
import kotlin.math.abs

@OptIn(ExperimentalForeignApi::class) // Обязательно добавь это!
@Composable
actual fun MapsLayout(
    stations: List<Station>,
    selectedStationState: SelectedStationState,
    onMapReady: () -> Unit,
    userLocation: Location?
) {
    UIKitView(
        modifier = Modifier.fillMaxSize(),
        interactive = true, // важно для жестов
        factory = {
            MKMapView().apply {
                // Правильные setter'ы вместо прямого присваивания
                setZoomEnabled(true)
                setScrollEnabled(true)
                setRotateEnabled(true)
                setPitchEnabled(true) // опционально — наклон карты
                showsUserLocation = true // синяя точка и отслеживание

                // Центрируем на пользователе или на Ташкенте
                val centerCoord = userLocation?.let {
                    CLLocationCoordinate2DMake(it.latitude, it.longitude)
                } ?: CLLocationCoordinate2DMake(41.311081, 69.240562)

                val region = MKCoordinateRegionMakeWithDistance(centerCoord, 20_000.0, 20_000.0)
                setRegion(region, animated = false)

                // Добавляем маркеры
                stations.forEach { station ->
                    val annotation = MKPointAnnotation().apply {
                        setCoordinate(CLLocationCoordinate2DMake(station.latitude, station.longitude))
                        setTitle(station.name)
                        setSubtitle(station.address ?: "Адрес не указан")
                    }
                    addAnnotation(annotation)
                }

                // Делегат для кликов по маркерам
                delegate = object : NSObject(), MKMapViewDelegateProtocol {
                    override fun mapView(mapView: MKMapView, didSelectAnnotationView: MKAnnotationView) {
                        val annotation = didSelectAnnotationView.annotation as? MKPointAnnotation
                        val coord = annotation?.coordinate ?: return

                        // Правильный доступ к полям структуры через useContents
                        coord.useContents {
                            val clickedStation = stations.find { station ->
                                abs(station.latitude - this.latitude) < 0.0001 &&
                                        abs(station.longitude - this.longitude) < 0.0001
                            }

                            clickedStation?.let {
                                selectedStationState.station = it
                                selectedStationState.showDialog = true
                            }
                        }
                    }
                }

                onMapReady()
            }
        },
        update = { mapView ->
            // Здесь можно обновлять регион или аннотации при изменении stations/userLocation
            // Но для простоты пока оставляем пустым
        }
    )
}