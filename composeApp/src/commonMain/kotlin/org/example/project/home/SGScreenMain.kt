package org.example.project.home

import InfoScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ecooil_kmp.composeapp.generated.resources.Res
import ecooil_kmp.composeapp.generated.resources.ecooil_text
import ecooil_kmp.composeapp.generated.resources.ic_ai92
import ecooil_kmp.composeapp.generated.resources.icon_dt
import ecooil_kmp.composeapp.generated.resources.notification
import ecooil_kmp.composeapp.generated.resources.right_arrow
import ecooil_kmp.composeapp.generated.resources.user
import org.example.data.ApiCallResult
import org.example.data.CarResponse
import org.example.data.TransactionsResponse
import org.example.networking.Constant
import org.example.networking.InsultCensorClient
import org.example.project.history.TransactionsScreen
import org.example.project.history.TransactionsScreenParent
import org.example.project.login.AuthScreen
import org.example.project.until.ShimmerEffect
import org.example.util.AppSettings
import org.example.util.NetworkError
import org.example.util.onError
import org.example.util.onSuccess
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
@Preview
fun SGScreenMain(
    client: InsultCensorClient? = null,
    viewModel: HomeViewModel? = null  // ← сделай nullable и дай дефолт null
) {
    val scroll = rememberScrollState()
    val navigator = LocalNavigator.current

    // Безопасно берём значения, с fallback на заглушки для превью
    val userName = viewModel?.userName ?: "User"
    val bonusText = viewModel?.bonusText ?: "0"
    val balanceText = viewModel?.balanceText ?: "0 смн"
    val statusText = viewModel?.statusText ?: "SG"
    val fuelItems2 = viewModel?.fuelItems ?: emptyList() // или создай тестовые
    val tokenError = viewModel?.tokenError ?: false
    val isRefresh = viewModel?.isRefreshMainScreen ?: false

    PullToRefreshBox(
        isRefreshing = isRefresh,
        onRefresh = { viewModel?.refresh() }
    ) {

        if (tokenError) {
            AppSettings.clear()
            val navigatorLogOut = LocalNavigator.currentOrThrow
            navigatorLogOut.parent?.replaceAll(AuthScreen)
        }

        // Ваш UI
    _root_ide_package_.org.example.project.GradientBackground(showVersion = false) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
        ) {
            HomeTopBar(
                userName = userName,
                onProfileClick = {
                    navigator?.parent?.push(InfoScreen)
                    /* открыть профиль */
                },
                onNotificationClick = { /* открыть уведомления */ }
            )

            // ---- TOP CARD ----
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 34.dp, end = 34.dp, top = 5.dp, bottom = 22.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 24.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ecooil_text),
                        contentDescription = "EcoOil",
                        modifier = Modifier.height(60.dp)
                            .padding(18.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        BalanceItem(
                            label = "БОНУС",
                            amount = bonusText,
                            amountColor = Color(0xFFFF6B00)
                        )
                        Spacer(
                            modifier = Modifier.width(1.dp).height(40.dp)
                                .background(Color(0xFFE0E0E0))
                        )
                        BalanceItem(
                            label = "БАЛАНС",
                            amount = balanceText,
                            amountColor = Color(0xFF00A8A8)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Status / $statusText",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(Modifier.height(2.dp))

            // ---- АКЦИИ ----
            Text(
                "Акции", fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(16.dp, 0.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                modifier = Modifier.padding(vertical = 12.dp),

                ) {
                items(5) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(80.dp),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.icon_dt),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,

                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ---- ТОПЛИВО ----
            Text(
                "Деньги на топливо", fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(16.dp, 0.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                modifier = Modifier.padding(top = 12.dp)
            ) {
                items(fuelItems2) { item ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.size(60.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Image(
                                    painter = painterResource(item.icon),
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(75.dp)
                                )
                            }

                        }

                        Spacer(Modifier.height(8.dp))

                        Text(item.title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(item.value, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

//            // ---- ИСТОРИЯ ----
//            Row(
//                Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    "История", fontSize = 16.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(16.dp, 0.dp)
//                )
//                Icon(
//                    painter = painterResource(Res.drawable.right_arrow),
//                    contentDescription = null,
//                    modifier = Modifier.height(30.dp)
//                        .padding(end = 16.dp, top = 5.dp, bottom = 5.dp, start = 0.dp)
//                )
//            }

            Spacer(Modifier.height(12.dp))


            val navigator = LocalNavigator.current

            LastTransactionsCard(
                transactions = viewModel!!.transactions,
                onOpenTransactions = {
                    navigator?.parent?.push(TransactionsScreenParent(client!!))
                },
                onOpenTransactionDetails = { tx ->
                    navigator?.parent?.push(TransactionDetailsScreen(tx))
                }
            )

        }
    }
    }
}

@Composable
fun HomeTopBar(
    userName: String = "Test",
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 50.dp, bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 👤 Аватар
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0x99FFFFFF))
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.user),
                            contentDescription = null,
                            tint = Color(0xFF1E88E5),
                            modifier = Modifier
                                .size(38.dp)
                                .padding(7.dp)
                        )
                    }


            }

            Spacer(modifier = Modifier.width(12.dp))

            // 👤 Имя
            Text(
                text = userName,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.weight(1f))

            // 🔔 Уведомления
            IconButton(onClick = onNotificationClick) {
                Icon(
                    painter = painterResource(Res.drawable.notification),
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
fun BalanceItem(label: String, amount: String, amountColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 13.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = amount, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = amountColor)
    }
}

suspend fun requestCarData(
    client: InsultCensorClient?,
    onSuccess: (CarResponse) -> Unit,
    onError: (NetworkError?) -> Unit
) {
    try {
        val hash = org.example.project.Until.sha256(
            AppSettings.getString("token") +
                    AppSettings.getInt("car_id") +
                    org.example.project.Until.getDeviceId()
        )

        val map = hashMapOf(
            "Token" to AppSettings.getString("token"),
            "DeviceId" to org.example.project.Until.getDeviceId(),
            "CarId" to AppSettings.getInt("car_id").toString(),
            "Limit" to 0,
            "Hash" to hash
        )

        val result = client?.request<CarResponse>(
            path = Constant.getCar,
            params = map,
        )

        result?.onSuccess { body ->
                onSuccess(body)
        }?.onError { e ->
            onError(e)
        } ?: run {
            onError(null)
        }
    } catch (e: Throwable) {
        onError(null)
    }
}