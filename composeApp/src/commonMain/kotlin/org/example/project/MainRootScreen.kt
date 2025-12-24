package org.example.project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.*
import ecooil_kmp.composeapp.generated.resources.Res
import ecooil_kmp.composeapp.generated.resources.home
import ecooil_kmp.composeapp.generated.resources.icmap
import ecooil_kmp.composeapp.generated.resources.icqr_code
import org.example.data.MapStation
import org.example.networking.Constant
import org.example.networking.InsultCensorClient
import org.example.networking.createHttpClient
import org.example.project.MainRootScreen.LocalHomeVm
import org.example.project.MainRootScreen.LocalQrVm
import org.example.project.home.HomeViewModel
import org.example.project.home.SGScreenMain
import org.example.project.map.MapScreen
import org.example.project.map.getStationsMap
import org.example.project.map.getStationsMapCached
import org.example.project.qr.QrScreen
import org.example.project.qr.QrViewModel
import org.jetbrains.compose.resources.painterResource

object MainRootScreen : Screen {

    @Composable
    override fun Content() {
        val client = remember {
            InsultCensorClient(
                createHttpClient(PlatformHttpEngine()),
                baseUrl = Constant.baseUrl
            )
        }
        val viewModel: HomeViewModel = remember {
            HomeViewModel(client)
        }

        val qrViewModel = remember {
            QrViewModel()
        }


        TabNavigator(HomeTab) { tabNavigator ->

            Scaffold(
                contentWindowInsets = WindowInsets(0), // 🔥 ВАЖНО

                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = tabNavigator.current == HomeTab,
                            onClick = { tabNavigator.current = HomeTab },
                            icon = { Icon(HomeTab.options.icon!!, null,
                                modifier = Modifier.size(24.dp)) },
                            label = { Text("Главная") }
                        )

                        NavigationBarItem(
                            selected = tabNavigator.current == QrTab,
                            onClick = { tabNavigator.current = QrTab },
                            icon = { Icon(QrTab.options.icon!!, null,
                                modifier = Modifier.size(24.dp)) },
                            label = { Text("QR") }
                        )

                        NavigationBarItem(
                            selected = tabNavigator.current == SGTab,
                            onClick = { tabNavigator.current = SGTab },
                            icon = { Icon(SGTab.options.icon!!, null,
                                modifier = Modifier.size(24.dp)) },
                            label = { Text("АЗС") }
                        )
                    }
                }
            ) { padding ->
                Box(Modifier.padding(padding)) {
                    CompositionLocalProvider(
                        LocalClient provides client,
                        LocalHomeVm provides viewModel,
                        LocalQrVm provides qrViewModel
                    ) {
                        CurrentTab()
                    }
                }
            }
        }
    }
    val LocalHomeVm = staticCompositionLocalOf<HomeViewModel?> {
        null
    }


    val LocalClient = staticCompositionLocalOf<InsultCensorClient?> {
        null
    }

    val LocalQrVm = staticCompositionLocalOf<QrViewModel> {
        error("QrViewModel not provided")
    }


}
object HomeTab : Tab {
    override val options: TabOptions
        @Composable get() {
            val title = "Home"
            val icon = painterResource(Res.drawable.home)
            return TabOptions(index = 0u, title, icon)
        }

    @Composable
    override fun Content() {
        val client = MainRootScreen.LocalClient.current

        val vm = LocalHomeVm.current
        SGScreenMain(client = client, viewModel = vm!!)
    }
}

object QrTab : Tab {
    override val options: TabOptions
        @Composable get() {
            val title = "Second"
            val icon = painterResource(Res.drawable.icqr_code)
            return TabOptions(index = 1u, title, icon)
        }
    @Composable
    override fun Content() {
        val vm = LocalQrVm.current

        // При каждом входе в таб — гарантируем refresh (если истёк) + запуск таймера
        LaunchedEffect(Unit) {
            vm.ensureCountdownRunning()
        }

        QrScreen.Content()
    }
}
object SGTab : Tab {
    override val options: TabOptions
        @Composable get() {
            val title = "Profile"
            val icon = painterResource(Res.drawable.icmap)
            return TabOptions(index = 2u, title, icon)
        }

    @Composable
    override fun Content() {
        val client = MainRootScreen.LocalClient.current

        var stations by remember { mutableStateOf<List<MapStation>>(emptyList()) }
        var error by remember { mutableStateOf<String?>(null) }
        var loading by remember { mutableStateOf(false) }

        // 1) При входе — пробуем кэш, иначе сеть
        var reloadKey by remember { mutableStateOf(0) }

        LaunchedEffect(reloadKey) {
            loading = true
            getStationsMapCached(
                client = client,
                forceRefresh = false,
                allowStaleIfOffline = true,
                onSuccess = {
                    stations = it
                    error = null
                    loading = false
                },
                onError = { e ->
                    error = e?.message ?: "Не удалось получить данные. Повторите попытку позже."
                    loading = false
                }
            )
        }

        when {
            loading -> CenteredLoading(
                "Загрузка карты..."
            )
            error != null ->CenteredError(
                message = error!!,
                onRetry = {reloadKey++}
            )
            else -> MapScreen(stations = stations, userLocation = null)
        }
    }
}


@Composable
fun CenteredError(
    message: String,
    onRetry: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Не удалось загрузить карту",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            if (onRetry != null) {
                Spacer(Modifier.height(16.dp))
                Button(onClick = onRetry) {
                    Text("Повторить")
                }
            }
        }
    }
}


@Composable
fun CenteredLoading(
    text: String = "Загрузка…"
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()

            Spacer(Modifier.height(12.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}