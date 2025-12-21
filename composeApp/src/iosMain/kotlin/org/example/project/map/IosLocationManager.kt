package org.example.project.map

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.*
import platform.darwin.NSObject

class IosLocationManager : NSObject(), CLLocationManagerDelegateProtocol {

    private val manager = CLLocationManager()

    var onLocation: ((Double, Double) -> Unit)? = null
    var onAuthChanged: ((CLAuthorizationStatus) -> Unit)? = null

    init {
        manager.delegate = this
        manager.desiredAccuracy = kCLLocationAccuracyBest
    }

    fun requestPermissionAndStart() {
        manager.requestWhenInUseAuthorization()
        manager.startUpdatingLocation()
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun locationManager(
        manager: CLLocationManager,
        didUpdateLocations: List<*>
    ) {
        val loc = didUpdateLocations.lastOrNull() as? CLLocation ?: return

        loc.coordinate.useContents {
            onLocation?.invoke(latitude, longitude)
        }
    }

    override fun locationManager(
        manager: CLLocationManager,
        didChangeAuthorizationStatus: CLAuthorizationStatus
    ) {
        onAuthChanged?.invoke(didChangeAuthorizationStatus)

        if (
            didChangeAuthorizationStatus == kCLAuthorizationStatusAuthorizedWhenInUse ||
            didChangeAuthorizationStatus == kCLAuthorizationStatusAuthorizedAlways
        ) {
            manager.startUpdatingLocation()
        }
    }
}