package org.example.project.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class, ExperimentalComposeUiApi::class)
@Composable
actual fun MapsLayout() {
    UIKitView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            val mapView = MKMapView().apply {
                val singapore = CLLocationCoordinate2DMake(1.35, 103.87)

                val region = MKCoordinateRegionMakeWithDistance(
                    centerCoordinate = singapore,
                    latitudinalMeters = 50_000.0,
                    longitudinalMeters = 50_000.0
                )

                setRegion(region, animated = false)

                val annotation = MKPointAnnotation().apply {
                    setCoordinate(singapore)
                }

                addAnnotation(annotation)
            }

            mapView
        }
    )
}