package org.example.project
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.example.data.CarResponse
import org.example.networking.Constant
import org.example.networking.InsultCensorClient
import org.example.networking.createHttpClient
import org.example.util.AppSettings
import org.example.util.onError
import org.example.util.onSuccess
import org.jetbrains.compose.ui.tooling.preview.Preview


object QrScreen : Screen {

    @Composable
    override fun Content() {

        val scope = rememberCoroutineScope()
        val client = remember {
            InsultCensorClient(
                createHttpClient(PlatformHttpEngine()),
                baseUrl = Constant.baseUrl
            )
        }

        var secondsLeft by remember { mutableStateOf(180) }
        var qrValue by remember { mutableStateOf("") }

        // QR Painter
        val qrPainter = rememberQrCodePainter(qrValue)

        LaunchedEffect(Unit) {
            while (isActive) {

                // 1. Генерируем QR
                val carNumber = AppSettings.getString("car_number")
                val carId = AppSettings.getInt("car_id")
                qrValue = "$carNumber$carId"

                // 2. Запрос на сервер
                loadData(scope, client)

                // 3. Таймер 3 минуты
                secondsLeft = 180
                while (secondsLeft > 0) {
                    delay(1000)
                    secondsLeft--
                }
            }
        }

        GradientBackground(showVersion = false) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Image(
                        painter = qrPainter,
                        contentDescription = "QR Code",
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .aspectRatio(1f)
                    )

                    Spacer(Modifier.height(20.dp))

                    Text(
                        text = "QR актуален: ${secondsLeft / 60}:${(secondsLeft % 60).toString()
                            .padStart(2, '0')}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}


 fun loadData(scope: CoroutineScope, client: InsultCensorClient) {
     scope.launch {
          requestQrData(
              client = client,
              onSuccess = { body ->

              },
              onError = {})
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

@Preview(showBackground = true)
@Composable
fun QrScreenPreview() {
    QrScreen.Content()
}
