package org.example.project.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import org.example.data.Location
import org.example.data.MapStation
import org.example.data.SelectedStationState
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.Foundation.NSURL
import platform.MapKit.*
import platform.UIKit.UIApplication
import platform.darwin.NSObject
import kotlin.math.abs

@OptIn(ExperimentalForeignApi::class)
private fun khujandCoord() = CLLocationCoordinate2DMake(40.2833, 69.6167)

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapsLayout(
    stations: List<MapStation>,
    selectedStationState: SelectedStationState,
    onMapReady: () -> Unit,
    userLocation: Location?
) {
    // локально получаем гео на iOS (если userLocation не прокидываешь сверху)
    var myLocation by remember { mutableStateOf<Location?>(null) }
    val locManager = remember { IosLocationManager() }

    LaunchedEffect(Unit) {
        locManager.onLocation = { lat, lng ->
            myLocation = Location(lat, lng)
        }
        locManager.requestPermissionAndStart()
    }

    val effectiveLocation = myLocation ?: userLocation

    UIKitView(
        modifier = Modifier.fillMaxSize(),
        interactive = true,
        factory = {
            MKMapView().apply {
                setZoomEnabled(true)
                setScrollEnabled(true)
                setRotateEnabled(true)
                setPitchEnabled(true)

                showsUserLocation = true

                // старт: userLocation если есть, иначе Худжанд
                val center = effectiveLocation?.let {
                    CLLocationCoordinate2DMake(it.latitude, it.longitude)
                } ?: khujandCoord()

                setRegion(
                    MKCoordinateRegionMakeWithDistance(center, 20_000.0, 20_000.0),
                    animated = false
                )

                // маркеры
                stations.forEach { st ->
                    val a = MKPointAnnotation().apply {
                        setCoordinate(CLLocationCoordinate2DMake(st.latitude, st.longitude))
                        setTitle(st.name)
                        setSubtitle(st.snippet)
                    }
                    addAnnotation(a)
                }

                delegate = object : NSObject(), MKMapViewDelegateProtocol {

                    override fun mapView(mapView: MKMapView, didSelectAnnotationView: MKAnnotationView) {
                        val ann = didSelectAnnotationView.annotation ?: return

                        // Игнорируем клик по синей точке пользователя
                        if (ann is MKUserLocation) return

                        val point = ann as? MKPointAnnotation ?: return
                        val coord = point.coordinate

                        coord.useContents {
                            val clicked = stations.find { st ->
                                abs(st.latitude - this.latitude) < 0.00001 &&
                                        abs(st.longitude - this.longitude) < 0.00001
                            }
                            clicked?.let {
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
            // обновляем центр когда получили user location
            val center = effectiveLocation?.let {
                CLLocationCoordinate2DMake(it.latitude, it.longitude)
            } ?: khujandCoord()

            mapView.setRegion(
                MKCoordinateRegionMakeWithDistance(center, 20_000.0, 20_000.0),
                animated = true
            )// перерисуем аннотации без дублей (кроме user location)
            val anns = mapView.annotations ?: emptyList<Any>()
            val toRemove = anns.filterNot { it is MKUserLocation }
            mapView.removeAnnotations(toRemove)

            stations.forEach { st ->
                val a = MKPointAnnotation().apply {
                    setCoordinate(CLLocationCoordinate2DMake(st.latitude, st.longitude))
                    setTitle(st.name)
                    setSubtitle(st.snippet)
                }
                mapView.addAnnotation(a)
            }
        }
    )
}



actual object RouteNavigator {
    actual fun openRoute(from: Location?, to: Location, title: String?) {
        val urlString = if (from != null) {
            "http://maps.apple.com/?saddr=${from.latitude},${from.longitude}&daddr=${to.latitude},${to.longitude}"
        } else {
            "http://maps.apple.com/?ll=${to.latitude},${to.longitude}&q=${(title ?: "Точка").replace(" ", "+")}"
        }

        val url = NSURL.URLWithString(urlString) ?: return
        UIApplication.sharedApplication.openURL(url)
    }
}