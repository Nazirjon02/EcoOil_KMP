package org.example.project

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ecooil_kmp.composeapp.generated.resources.Res
import ecooil_kmp.composeapp.generated.resources.ecooil_text
import ecooil_kmp.composeapp.generated.resources.home
import ecooil_kmp.composeapp.generated.resources.map
import ecooil_kmp.composeapp.generated.resources.right_arrow
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun EcoOilMainScreen() {
    val scrollState = rememberScrollState()

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF00A8A8))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = 80.dp) // место под bottom bar
            ) {
                // === Верхняя карточка с балансом ===
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 24.dp)
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.ecooil_text),
                            contentDescription = "EcoOil",
                            modifier = Modifier.height(60.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            BalanceItem(label = "БОНУС", amount = "10.3 C", amountColor = Color(0xFFFF6B00))
                            Spacer(modifier = Modifier.width(1.dp).height(40.dp).background(Color(0xFFE0E0E0)))
                            BalanceItem(label = "БАЛАНС", amount = "20.0 C", amountColor = Color(0xFF00A8A8))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "SG Lite",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black.copy(alpha = 0.7f)
                        )
                    }
                }

                // === Акции ===
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .clickable { /* открыть акции */ },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Акции", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    Icon(
                        painterResource(Res.drawable.right_arrow),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.width(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // === Цены на топливо ===
                Text(
                    "Цены на топливо",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp)
                ) {
                    items(fuelList) { fuel ->
                        FuelPriceCard(fuel = fuel)
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // === История ===
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .clickable { /* открыть историю */ },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("История", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    Icon(painterResource(Res.drawable.right_arrow), contentDescription = null, tint = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(180.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            painterResource(Res.drawable.right_arrow),
                            contentDescription = null,
                            tint = Color(0xFF00A8A8).copy(alpha = 0.6f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Нет истории транзакций",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }

            // === Bottom Navigation Bar ===
            BottomNavigationBar(
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun BalanceItem(label: String, amount: String, amountColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = amount, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = amountColor)
    }
}

data class Fuel(
    val name: String,
    val shortName: String,
    val price: String,
    val iconRes: DrawableResource,
    val bgColor: Color
)

val fuelList = listOf(
    Fuel("АИ-95", "95", "0.00 смн", Res.drawable.home, Color(0xFFFFF3E0)),
    Fuel("АИ-92", "92", "0.00 смн", Res.drawable.home, Color(0xFFE0F7FA)),
    Fuel("ДТ", "ДТ", "0.00 смн", Res.drawable.home, Color(0xFF212121)),
    Fuel("ДТ-ЭКТО", "ЭКТО", "0.00 смн", Res.drawable.home, Color(0xFFF5F5F5)),
    Fuel("ГАЗ", "ГАЗ", "0.00 смн", Res.drawable.home, Color(0xFF424242)),
)

@Composable
fun FuelPriceCard(fuel: Fuel) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = fuel.bgColor),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier.size(width = 100.dp, height = 120.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(fuel.iconRes),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(fuel.shortName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(fuel.price, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun BottomNavigationBar(modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF00D4D4)),
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(icon = painterResource(Res.drawable.home), label = "Главная", selected = true)
            BottomNavItem(icon =painterResource(Res.drawable.map), label = "QR", selected = false)
            BottomNavItem(icon = painterResource(Res.drawable.map), label = "АЗС", selected = false)
        }
    }
}

@Composable
fun BottomNavItem(icon: Painter, label: String, selected: Boolean) {
    val tint = if (selected) Color.White else Color.White.copy(alpha = 0.6f)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = tint, fontSize = 12.sp)
    }
}