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

@Composable
actual fun MapsLayout(
    stations: List<Station>,
    selectedStationState: SelectedStationState,
    onMapReady: () -> Unit,
    userLocation: Location?
) {
    UIKitView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            MKMapView().apply {
                showsUserLocation = true // синяя точка + отслеживание
                isZoomEnabled = true
                isScrollEnabled = true
                isRotateEnabled = true

                // Центр на пользователе или на Ташкенте
                val center = userLocation?.let {
                    CLLocationCoordinate2DMake(it.latitude, it.longitude)
                } ?: CLLocationCoordinate2DMake(41.311081, 69.240562)

                val region = MKCoordinateRegionMakeWithDistance(center, 20_000.0, 20_000.0)
                setRegion(region, animated = false)

                // Добавляем аннотации (маркеры)
                stations.forEach { station ->
                    val annotation = MKPointAnnotation().apply {
                        setCoordinate(CLLocationCoordinate2DMake(station.latitude, station.longitude))
                        title = station.name
                        subtitle = station.address ?: "Адрес не указан"
                    }
                    addAnnotation(annotation)
                }

                // Делегат для обработки кликов по маркерам
                delegate = object : NSObject(), MKMapViewDelegateProtocol {
                    override fun mapView(mapView: MKMapView, didSelectAnnotationView: MKAnnotationView) {
                        val annotation = didSelectAnnotationView.annotation as? MKPointAnnotation
                        val coord = annotation?.coordinate ?: return

                        // Ищем станцию по координатам
                        val clickedStation = stations.find {
                            kotlin.math.abs(it.latitude - coord.latitude) < 0.0001 &&
                                    kotlin.math.abs(it.longitude - coord.longitude) < 0.0001
                        }

                        clickedStation?.let {
                            selectedStationState.station = it
                            selectedStationState.showDialog = true
                        }
                    }
                }

                onMapReady()
            }
        },
        update = { mapView ->
            // Можно обновлять при изменении данных
        }
    )
}