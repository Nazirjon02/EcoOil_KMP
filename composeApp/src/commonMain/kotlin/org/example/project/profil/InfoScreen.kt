import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import ecooil_kmp.composeapp.generated.resources.Res
import ecooil_kmp.composeapp.generated.resources.arrow_back_ios
import ecooil_kmp.composeapp.generated.resources.call
import ecooil_kmp.composeapp.generated.resources.communication
import ecooil_kmp.composeapp.generated.resources.home_info
import ecooil_kmp.composeapp.generated.resources.right_arrow
import org.example.project.AuthScreen
import org.example.util.AppSettings
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

// ------------------------- InfoScreen -------------------------
object InfoScreen : Tab {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        InfoScreenContent(
            onExit = {
                AppSettings.clear()           // Очистка токена
                navigator.replaceAll(AuthScreen) // Переход на AuthScreen
            },
            onBackAction={
                navigator.pop()
            }
        )
    }

    override val options: TabOptions
        @Composable
        get()  {
            val icon = painterResource(Res.drawable.right_arrow)
        return TabOptions(
            index = 0u,
            title = "Info",
            icon=icon
        )
        }
}

// ------------------------- InfoScreenContent -------------------------
@Composable
@Preview(showBackground = true)
fun InfoScreenContent(onExit: () -> Unit = {} ,onBackAction: () -> Unit = {} ) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        ProfileTopBar(onBack = { onBackAction() })

        // ──────── Профиль ────────
        Card(
            modifier = Modifier.fillMaxWidth(),

            colors = CardDefaults.cardColors(containerColor = Color(0xFFDFE6EE)),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(85.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2089CE)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text =AppSettings.getString("car_name","****")[0].toString(),
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "EcoOil",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF092F48)
                )
                Text(
                    AppSettings.getString("car_name","****"),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        // ──────── Личная информация ────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Личная информация",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2089CE),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                InfoRow(label = "Номер авто", value = AppSettings.getString("car_number","******"))
                Divider(
                    thickness = 1.dp,
                    color = Color(0xFFE0E0E0),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                InfoRow(label = "Номер телефона", value = AppSettings.getString("car_phone_number","92****"))
                Divider(
                    thickness = 1.dp,
                    color = Color(0xFFE0E0E0),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                InfoRow(label = "Дата рождения", value = AppSettings.getString("car_birth_date_time","**.**.**").substringBefore(" "))
                Divider(
                    thickness = 1.dp,
                    color = Color(0xFFE0E0E0),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                InfoRow(label = "Тип статуса", value = AppSettings.getString("pip_status_type","****"))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ──────── Контакты ────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                val uriHandler = LocalUriHandler.current
                ContactRow(
                    text = "Тоҷикистон, г. Душанбе, ул. Баховуддин 15",
                    icon = Res.drawable.home_info,
                ){
                    uriHandler.openUri("geo:38.552573,68.851528")
                }

               Spacer(modifier = Modifier.height(10.dp))

                ContactRow(
                    text = "4800",
                    icon = Res.drawable.call
                ){
                    uriHandler.openUri("tel:4800")
                }

                Spacer(modifier = Modifier.height(10.dp))

                ContactRow(
                    text = "info@ecooil.tj",
                    icon = Res.drawable.communication
                ){
                    uriHandler.openUri("mailto:info@ecooil.tj")
                }
            }

        }


        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = onExit, // Переход через callback
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp)
                .height(50.dp),

            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E88E5),
                disabledContainerColor = Color(0xFFAAD7D7)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Exit",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}



// ------------------------- Универсальные компоненты -------------------------
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ContactRow(
    text: String,
    icon: DrawableResource,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.size(45.dp),
            colors = CardDefaults.cardColors(Color(0x46969B9E))
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = Color(0xFF1E88E5),
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(Modifier.width(16.dp))

        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ProfileTopBar(
    onBack: () -> Unit = {},
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 0.dp, end = 12.dp, top = 40.dp, bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_back_ios),
                        contentDescription = null,
                        tint = Color(0xFF1E88E5),
                        modifier = Modifier
                            .size(40.dp)
                            .padding(7.dp)
                    )
                }


            }

            Spacer(modifier = Modifier.width(12.dp))

        }
    }
}


