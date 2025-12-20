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

    var secondsLeft by mutableStateOf(0)  // стартуем с 0
        private set

    var qrValue by mutableStateOf("")
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    private var countdownJob: Job? = null

    // Проверяем и обновляем QR, если таймер истёк или QR пустой
    fun refreshIfNeeded() {
        if (secondsLeft <= 0 || qrValue.isEmpty()) {
            refreshNow()
        }
    }

    // Запускаем таймер, если его нет (даже если secondsLeft == 0 — но после refresh будет 180)
    private fun startCountdown() {
        if (countdownJob?.isActive == true) return

        countdownJob = viewModelScope.launch {
            while (secondsLeft > 0) {
                delay(1000)
                secondsLeft--
            }
            // Когда дошёл до 0 — Job сам завершается
        }
    }

    private fun refreshNow() {
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
                // Успешно — сбрасываем таймер и запускаем countdown
                secondsLeft = 180
                startCountdown()  // ← сразу запускаем таймер после обновления
            } finally {
                isRefreshing = false
            }
        }
    }

    // Вызываем из таба: сначала refresh (если нужно), потом гарантированно стартуем таймер
    fun ensureCountdownRunning() {
        refreshIfNeeded()
        startCountdown()  // всегда запускаем (если уже есть — не дублирует)
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
