package org.example.project
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import cafe.adriel.voyager.core.screen.Screen
import ecooil_kmp.composeapp.generated.resources.Res
import ecooil_kmp.composeapp.generated.resources.bg_ecooil
import ecooil_kmp.composeapp.generated.resources.ecooil_text
import ecooil_kmp.composeapp.generated.resources.playstore
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

object QrScreen : Screen {
    @Composable
    override fun Content() {
        GradientBackground(showVersion = false) {
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

@Preview(showBackground = true)
@Composable
fun QrScreenPreview() {
    QrScreen.Content()
}
