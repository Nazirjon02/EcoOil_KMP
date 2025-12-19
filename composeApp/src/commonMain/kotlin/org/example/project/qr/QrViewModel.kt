package org.example.project.qr

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.data.CarResponse
import org.example.networking.Constant
import org.example.networking.InsultCensorClient
import org.example.project.Until
import org.example.util.AppSettings
import org.example.util.onError
import org.example.util.onSuccess

class QrViewModel(
    private val client: InsultCensorClient
) : ViewModel() {

    var secondsLeft by mutableStateOf(180)
        private set

    var qrValue by mutableStateOf("")
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    private var loopJob: Job? = null


    fun start() {
        if (loopJob?.isActive == true) return

        loopJob = viewModelScope.launch {
            while (true) {
                refreshNow()
                secondsLeft = 180
                while (secondsLeft > 0) {
                    delay(1000)
                    secondsLeft--
                }
            }
        }
    }


    fun refreshNow() {
        if (isRefreshing) return
        isRefreshing = true

        // 1) генерим QR
        val carNumber = AppSettings.getString("car_number")
        val carId = AppSettings.getInt("car_id")
        qrValue = "$carNumber$carId"

        // 2) дергаем сервер
        viewModelScope.launch {
            try {
                requestQrData(
                    client = client,
                    onSuccess = { /* обработка body */ },
                    onError = { /* лог/ошибка */ }
                )
            } finally {
                isRefreshing = false
            }
        }
    }
}


suspend fun requestQrData(
    client: InsultCensorClient?,
    onSuccess: (CarResponse) -> Unit,
    onError: (Throwable?) -> Unit
) {
    try {
        val hash = Until.sha256(AppSettings.getInt("car_id").toString()
                +AppSettings.getString("token")
                + 0
                + Until.getDeviceId()
        )

        val map = hashMapOf(
            "Token" to AppSettings.getString("token"),
            "DeviceId" to Until.getDeviceId(),
            "CarId" to AppSettings.getInt("car_id").toString(),
            "Limit" to 0,
            "Hash" to hash
        )

        val result = client?.request<CarResponse>(
            path = Constant.getQr,
            params = map,
        )

        result?.onSuccess { body ->
            if (body.code == 1) {
                onSuccess(body)
            } else {
                onError(null)
            }
        }?.onError { e ->
        } ?: run {
            onError(null)
        }
    } catch (e: Throwable) {
        onError(e)
    }
}
