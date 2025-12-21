package org.example.project.qr

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import org.example.project.Until
import org.example.project.Until.currentTimeMillis
import org.example.util.AppSettings
import kotlin.time.ExperimentalTime

class QrViewModel : ViewModel() {

    var secondsLeft by mutableStateOf(180)
        private set

    var qrValue by mutableStateOf("")
        private set

    var isError by mutableStateOf(false)
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    private var countdownJob: Job? = null

    fun refreshIfNeeded() {
        if (secondsLeft <= 0 || qrValue.isEmpty()) {
            refreshNow()
        }
    }

    private fun startCountdown() {
        if (countdownJob?.isActive == true) return

        countdownJob = viewModelScope.launch {
            while (secondsLeft > 0) {
                delay(1000)
                secondsLeft--
            }

        }
    }

    private fun refreshNow() {
        if (isRefreshing) return
        isRefreshing = true

        viewModelScope.launch {
            try {
                val cardNumber = AppSettings.getString("card_number")
                val carId = AppSettings.getInt("car_id")

                // 1) добавляем timestamp
                val ts = nowQrTimestampPlus3Min() // yyyy-MM-dd HH:mm:ss
                println(ts)

                // 2) считаем hash (sha256)
                // Выбирай состав строки как тебе нужно. Я сделал: carId + token + ts + deviceId
                val hashInput = ts
                val hash = Until.sha256(hashInput)

                // 3) формируем QR payload (строка)
                // Можно поменять формат под твой сервер/сканер.
                qrValue = cardNumber + "," + carId + "," + hash

                // 4) состояние
                isError = false
                secondsLeft = 180
                startCountdown()
            } catch (_: Throwable) {
                // Если вдруг нет данных в AppSettings или ошибка sha
                isError = true
                secondsLeft = 0
                qrValue = ""
            } finally {
                isRefreshing = false
            }
        }
    }

    fun ensureCountdownRunning() {
        refreshIfNeeded()
        startCountdown()
    }
}




@OptIn(ExperimentalTime::class, ExperimentalTime::class)
fun nowQrTimestampPlus3Min(): String {
    val plus3MinMillis = currentTimeMillis() + 3L * 60L * 1000L

    val dt = Instant
        .fromEpochMilliseconds(plus3MinMillis)
        .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())

    return buildString {
        append(dt.year.toString().padStart(4, '0'))
        append("-")
        append(dt.monthNumber.toString().padStart(2, '0'))
        append("-")
        append(dt.dayOfMonth.toString().padStart(2, '0'))
        append(" ")
        append(dt.hour.toString().padStart(2, '0'))
        append(":")
        append(dt.minute.toString().padStart(2, '0'))
        append(":")
        append(dt.second.toString().padStart(2, '0'))
    }
}