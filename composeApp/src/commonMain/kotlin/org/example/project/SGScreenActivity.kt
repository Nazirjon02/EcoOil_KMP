package org.example.project

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun SGScreenContent() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        // ──────── Профиль ────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFDFE6EE)),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Простая круглая аватарка вместо иконки
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2089CE)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "T",
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Test", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF092F48))
                Text("EcoOil", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

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

                InfoRow(label = "Номер авто", value = "2222АА01")
                Divider(thickness = 1.dp, color = Color(0xFFE0E0E0), modifier = Modifier.padding(vertical = 12.dp))
                InfoRow(label = "Номер телефона", value = "92***")
                Divider(thickness = 1.dp, color = Color(0xFFE0E0E0), modifier = Modifier.padding(vertical = 12.dp))
                InfoRow(label = "Дата рождения", value = "24.05.2002")
                Divider(thickness = 1.dp, color = Color(0xFFE0E0E0), modifier = Modifier.padding(vertical = 12.dp))
                InfoRow(label = "Тип статуса", value = "SG Lite")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ──────── Контакты ────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                ContactRow(
                    circleColor = Color(0xFF4CAF50),
                    text = "Тоҷикистон, г. Душанбе, ул. Баховуддин 15"
                )
                Spacer(modifier = Modifier.height(24.dp))
                ContactRow(
                    circleColor = Color(0xFF2196F3),
                    text = "4800"
                )
                Spacer(modifier = Modifier.height(24.dp))
                ContactRow(
                    circleColor = Color(0xFFFF9800),
                    text = "info@ecooil.tj"
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

// ──────── Универсальные компоненты ────────

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
private fun ContactRow(circleColor: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Просто цветной кружок вместо иконки
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(circleColor)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

object SGScreen : Screen {
    @Composable
    override fun Content() {
        SGScreenContent()
    }
}