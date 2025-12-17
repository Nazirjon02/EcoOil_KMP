package org.example.project.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ecooil_kmp.composeapp.generated.resources.Res
import ecooil_kmp.composeapp.generated.resources.ecooil_text
import ecooil_kmp.composeapp.generated.resources.ic_ai92
import ecooil_kmp.composeapp.generated.resources.ic_ai95
import ecooil_kmp.composeapp.generated.resources.ic_dt
import ecooil_kmp.composeapp.generated.resources.ic_dtecto
import ecooil_kmp.composeapp.generated.resources.ic_gas
import ecooil_kmp.composeapp.generated.resources.icon_dt
import ecooil_kmp.composeapp.generated.resources.notification
import ecooil_kmp.composeapp.generated.resources.right_arrow
import ecooil_kmp.composeapp.generated.resources.user
import org.example.data.CarResponse
import org.example.data.FuelItem
import org.example.networking.Constant
import org.example.networking.InsultCensorClient
import org.example.util.AppSettings
import org.example.util.onError
import org.example.util.onSuccess
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
@Preview
fun SGScreenMain(client: InsultCensorClient?) {
    val scroll = rememberScrollState()
    // 🔹 State для данных экрана
    var userName by remember { mutableStateOf("Test") }
    var bonusText by remember { mutableStateOf("0.00 C") }
    var balanceText by remember { mutableStateOf("0.00 C") }
    var statusText by remember { mutableStateOf("Status / SG Lite") }
    var fuelItems2 by remember {
        mutableStateOf(
            listOf<FuelItem>()
        )
    }

    // 🔹 Загрузка данных (НЕ composable!)
    suspend fun loadData() {
        requestCarData(
            client = client,
            onSuccess = { body ->
                userName = body.data?.car_data?.car_name ?: userName
                bonusText = "${body.data?.car_data?.car_pip_size ?: 0} C"
                balanceText = "${body.data?.car_data?.car_balance_size ?: 0} C"
                statusText = body.data?.car_data?.pip_status_type.toString()
                val oil = body.data?.car_oil

                if (oil != null) {
                    fuelItems2 = listOf(
                        FuelItem("АИ-95", "${oil.ai95} cмн", Res.drawable.ic_ai95),
                        FuelItem("АИ-92", "${oil.ai92} cмн", Res.drawable.ic_ai92),
                        FuelItem("ДТ", "${oil.dt} cмн", Res.drawable.ic_dt),
                        FuelItem("ДТ-Экто", "${oil.dtecto} cмн", Res.drawable.ic_dtecto),
                        FuelItem("ГАЗ", "${oil.gas} cмн", Res.drawable.ic_gas)
                    )
                }
            },
            onError = {
                // при необходимости: лог / toast
            }
        )
    }

    // ✅ ВЫЗЫВАЕТСЯ 1 РАЗ при открытии экрана
    LaunchedEffect(Unit) {
        loadData()
    }
    _root_ide_package_.org.example.project.GradientBackground(showVersion = false) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
            //    .padding(16.dp)
        ) {
            HomeTopBar(
                userName = userName,
                onProfileClick = {

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
                    .padding(start = 24.dp, end = 24.dp, top = 5.dp, bottom = 32.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 24.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ecooil_text),
                        contentDescription = "EcoOil",
                        modifier = Modifier.height(60.dp)
                            .padding(16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        _root_ide_package_.org.example.project.BalanceItem(
                            label = "БОНУС",
                            amount = bonusText,
                            amountColor = Color(0xFFFF6B00)
                        )
                        Spacer(
                            modifier = Modifier.width(1.dp).height(40.dp)
                                .background(Color(0xFFE0E0E0))
                        )
                        _root_ide_package_.org.example.project.BalanceItem(
                            label = "БАЛАНС",
                            amount = balanceText,
                            amountColor = Color(0xFF00A8A8)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Status / $statusText",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(Modifier.height(2.dp))

            // ---- АКЦИИ ----
            Text(
                "Акции", fontSize = 22.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(16.dp, 0.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(start = 16.dp),
                modifier = Modifier.padding(vertical = 12.dp),

                ) {
                items(5) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.size(90.dp),
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
                "Деньги на топливо", fontSize = 22.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(16.dp, 0.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(start = 16.dp),
                modifier = Modifier.padding(top = 12.dp)
            ) {
                items(fuelItems2) { item ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.size(90.dp)
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

            // ---- ИСТОРИЯ ----
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "История", fontSize = 22.sp,
                    modifier = Modifier.padding(16.dp, 0.dp)
                )
                Icon(
                    painter = painterResource(Res.drawable.right_arrow),
                    contentDescription = null,
                    modifier = Modifier.height(35.dp)
                        .padding(end = 16.dp, top = 5.dp, bottom = 5.dp, start = 0.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    repeat(4) {
                        Card(
                            Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),

                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {

                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,

                                ) {

                                Row(verticalAlignment = Alignment.CenterVertically) {

                                    Card(
                                        shape = RoundedCornerShape(50),
                                        colors = CardDefaults.cardColors(Color(0xFFE3F2FD))
                                    ) {
                                        Icon(
                                            painter = painterResource(Res.drawable.ic_ai92),
                                            contentDescription = null,
                                            tint = Color(0xFF1E88E5),
                                            modifier = Modifier
                                                .size(40.dp)
                                                .padding(2.dp)
                                        )
                                    }

                                    Spacer(Modifier.width(16.dp))

                                    Column {
                                        Text(
                                            "Зачислено",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            "АИ-92 33,02л",
                                            fontSize = 13.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "+9,91 смн",
                                        fontSize = 16.sp,
                                        color = Color(0xFF2E7D32)
                                    )
                                    Text(
                                        "18.03.2025 16:02:14",
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
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
suspend fun requestCarData(
    client: InsultCensorClient?,
    onSuccess: (CarResponse) -> Unit,
    onError: (Throwable?) -> Unit
) {
    try {
        val hash = _root_ide_package_.org.example.project.Until.sha256(
            AppSettings.getString("token") +
                    AppSettings.getInt("car_id") +
                    _root_ide_package_.org.example.project.Until.getDeviceId()
        )

        val map = hashMapOf(
            "Token" to AppSettings.getString("token"),
            "DeviceId" to _root_ide_package_.org.example.project.Until.getDeviceId(),
            "CarId" to AppSettings.getInt("car_id").toString(),
            "Limit" to 0,
            "Hash" to hash
        )

        val result = client?.request<CarResponse>(
            path = Constant.getCar,
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