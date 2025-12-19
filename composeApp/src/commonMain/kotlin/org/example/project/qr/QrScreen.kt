package org.example.project.qr
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
import org.example.project.GradientBackground
import org.example.project.MainRootScreen.LocalQrVm
import org.example.project.PlatformHttpEngine
import org.example.project.Until
import org.example.util.AppSettings
import org.example.util.onError
import org.example.util.onSuccess
import org.jetbrains.compose.ui.tooling.preview.Preview

object QrScreen : Screen {

    @Composable
    override fun Content() {

        val vm = LocalQrVm.current

        // стартуем цикл ОДИН раз на жизнь VM
        LaunchedEffect(Unit) {
            vm.start()
        }

        val qrPainter = rememberQrCodePainter(vm.qrValue)

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
                        text = "QR актуален: ${vm.secondsLeft / 60}:${(vm.secondsLeft % 60).toString().padStart(2, '0')}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun QrScreenPreview() {
    QrScreen.Content()
}
