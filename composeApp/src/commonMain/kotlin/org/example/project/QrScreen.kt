package org.example.project
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import cafe.adriel.voyager.core.screen.Screen
import ecooil_kmp.composeapp.generated.resources.Res
import ecooil_kmp.composeapp.generated.resources.playstore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.example.data.CarResponse
import org.example.networking.Constant
import org.example.networking.InsultCensorClient
import org.example.networking.createHttpClient
import org.example.util.AppSettings
import org.example.util.onError
import org.example.util.onSuccess
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

object QrScreen : Screen {
    @Composable
    override fun Content() {
        GradientBackground(showVersion = false) {

            val client = remember {
                InsultCensorClient(
                    createHttpClient(PlatformHttpEngine()),
                    baseUrl = Constant.baseUrl
                )
            }
            val scope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                loadData(scope, client)
            }
            Box(
                modifier = Modifier
                    .fillMaxSize(), // экран полностью
                contentAlignment = Alignment.Center // по центру
            ) {
                Image(
                    painter = painterResource(Res.drawable.playstore),
                    contentDescription = "QR Code",
                    modifier = Modifier
                        .fillMaxWidth(0.8f) // занимает 80% ширины экрана
                        .aspectRatio(1f), // квадратное изображение

                    contentScale = ContentScale.Fit // масштабируется под экран
                )
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
