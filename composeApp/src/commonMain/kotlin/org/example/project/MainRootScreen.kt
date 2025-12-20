package org.example.project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.*
import ecooil_kmp.composeapp.generated.resources.Res
import ecooil_kmp.composeapp.generated.resources.home
import ecooil_kmp.composeapp.generated.resources.icmap
import ecooil_kmp.composeapp.generated.resources.icqr_code
import org.example.networking.Constant
import org.example.networking.InsultCensorClient
import org.example.networking.createHttpClient
import org.example.project.MainRootScreen.LocalHomeVm
import org.example.project.MainRootScreen.LocalQrVm
import org.example.project.home.HomeViewModel
import org.example.project.home.SGScreenMain
import org.example.project.map.MapScreen
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
            QrViewModel(client)
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
                            selected = tabNavigator.current == EmptyTab,
                            onClick = { tabNavigator.current = EmptyTab },
                            icon = { Icon(EmptyTab.options.icon!!, null,
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
        val vm = LocalHomeVm.current
        SGScreenMain(client = null, viewModel = vm!!)
    }
}

object EmptyTab : Tab {
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
        MapScreen.Content()
    }
}
