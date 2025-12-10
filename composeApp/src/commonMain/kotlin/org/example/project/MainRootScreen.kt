package org.example.project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.*
import ecooil_kmp.composeapp.generated.resources.Res
import ecooil_kmp.composeapp.generated.resources.home
import org.jetbrains.compose.resources.painterResource

object MainRootScreen : Screen {

    @Composable
    override fun Content() {
        TabNavigator(HomeTab) { tabNavigator ->

            Scaffold(
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = tabNavigator.current == HomeTab,
                            onClick = { tabNavigator.current = HomeTab },
                            icon = { Icon(HomeTab.options.icon!!, null) },
                            label = { Text(HomeTab.options.title) }
                        )

                        NavigationBarItem(
                            selected = tabNavigator.current == EmptyTab,
                            onClick = { tabNavigator.current = EmptyTab },
                            icon = { Icon(EmptyTab.options.icon!!, null) },
                            label = { Text(EmptyTab.options.title) }
                        )

                        NavigationBarItem(
                            selected = tabNavigator.current == SGTab,
                            onClick = { tabNavigator.current = SGTab },
                            icon = { Icon(SGTab.options.icon!!, null) },
                            label = { Text(SGTab.options.title) }
                        )
                    }
                }
            ) { padding ->
                Box(Modifier.padding(padding)) {
                    CurrentTab()
                }
            }
        }
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
        HomeScreen.Content()
    }
}

object EmptyTab : Tab {
    override val options: TabOptions
        @Composable get() {
            val title = "Second"
            val icon = painterResource(Res.drawable.home)
            return TabOptions(index = 1u, title, icon)
        }

    @Composable
    override fun Content() {
        EmptyScreen.Content()
    }
}

object SGTab : Tab {
    override val options: TabOptions
        @Composable get() {
            val title = "Profile"
            val icon = painterResource(Res.drawable.home)
            return TabOptions(index = 2u, title, icon)
        }

    @Composable
    override fun Content() {
        SGScreenContent()
    }
}
