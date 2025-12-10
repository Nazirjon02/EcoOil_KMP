package org.example.project

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ecooil_kmp.composeapp.generated.resources.Res
import ecooil_kmp.composeapp.generated.resources.ecooil_text
import ecooil_kmp.composeapp.generated.resources.right_arrow
import ecooil_kmp.composeapp.generated.resources.sms
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun SGScreenMain() {

    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(16.dp)
    ) {

        // ---- TOP CARD ----
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

        Spacer(Modifier.height(24.dp))

        // ---- АКЦИИ ----
        Text("Акции", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            items(5) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size(90.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ecooil_text),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ---- ТОПЛИВО ----
        Text("Деньги на топливо", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)

        val fuelItems = listOf(
            "АИ-95" to "0.00cмн",
            "АИ-92" to "0.00cмн",
            "ДТ" to "0.00cмн",
            "ДТ-Экто" to "0.00cмн",
            "ГАЗ" to "0.00cмн"
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(top = 12.dp)
        ) {
            items(fuelItems) { item ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.size(90.dp)
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.sms),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(item.first, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(item.second, fontSize = 12.sp, color = Color.Gray)
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
            Text("История", fontSize = 22.sp)
            Icon(
                painter = painterResource(Res.drawable.right_arrow),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

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
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Card(
                            shape = RoundedCornerShape(50),
                            colors = CardDefaults.cardColors(Color(0xFFE3F2FD))
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.sms),
                                contentDescription = null,
                                tint = Color(0xFF1E88E5),
                                modifier = Modifier.height(24.dp)
                            )
                        }

                        Spacer(Modifier.width(16.dp))

                        Column {
                            Text("Зачислено ", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            Text("АИ-92 33,02л ", fontSize = 13.sp, color = Color.Gray)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("+9,91 смн", fontSize = 16.sp, color = Color(0xFF2E7D32))
                        Text("18.03.2025 16:02:14", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}
