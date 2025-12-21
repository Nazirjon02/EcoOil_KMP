package org.example.project.map


import org.example.data.MapStation
import org.example.data.ResponseMap
import org.example.networking.Constant
import org.example.networking.InsultCensorClient
import org.example.util.AppSettings
import org.example.util.onError
import org.example.util.onSuccess


suspend fun getStationsMap(
    client: InsultCensorClient?,
    onSuccess: (List<MapStation>) -> Unit,
    onError: (Throwable?) -> Unit
) {
    try {
        val token = AppSettings.getString("token")
        val carId = AppSettings.getInt("car_id").toString()
        val deviceId = org.example.project.Until.getDeviceId()

        // hash в том же стиле, как у тебя (можешь поменять порядок, если backend требует)
        val hash = org.example.project.Until.sha256(
            carId + token + deviceId
        )

        val params = hashMapOf(
            "Token" to token,
            "DeviceId" to deviceId,
            "CarId" to carId,
            "Hash" to hash
        )

        val result = client?.request<ResponseMap>(
            path = Constant.getMap,   // убедись что это путь карты, не транзакций
            params = params
        )

        result?.onSuccess { body ->
            if (body.code == 1) {
                val stationsUi = body.data.list_station_map.map { it.toMapStation() }
                onSuccess(stationsUi)
            } else {
                onError(null)
            }
        }?.onError { e ->
            onError(null)
        } ?: run {
            onError(null)
        }
    } catch (e: Throwable) {
        onError(e)
    }
}