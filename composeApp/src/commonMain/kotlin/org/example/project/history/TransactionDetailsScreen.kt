package org.example.project.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ecooil_kmp.composeapp.generated.resources.Res
import ecooil_kmp.composeapp.generated.resources.arrow_back_ios
import org.example.data.TransactionDto
import org.example.data.amountUi
import org.example.data.dateNoSeconds
import org.example.data.payTitle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

class TransactionDetailsScreen(
    private val tx: TransactionDto,
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    @Preview
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        // ✅ Жест/системная кнопка "Назад" (Android back, iOS swipe-back где поддерживается)
//        BackHandler(enabled = true) {
//            navigator.pop()
//        }


        val isShop = (tx.oilTypeName == "0")
        (tx.oilPrice == "0")
        (tx.trkId == 0)

        val title = if (isShop) "Чек: Магазин" else "Чек: Заправка"
        val fuelType = if (isShop) "Магазин" else tx.oilTypeName.orDash()
        val fuelPrice = if (isShop) "—" else tx.oilPrice.orDash()
        val liters = if (isShop) "—" else tx.oilSizeCompleted.orDash()

        // Бонусы
        val bonusOp = tx.pipSizeCompleted.orZero()      // бонус по операции
        val bonusTotal = tx.carPipSize.orZero()         // общий бонус клиента (если нужно)

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Детали операции") },
                    navigationIcon = {
                        IconButton(onClick = {
                            navigator.pop()
                        }) {
                            Icon(
                                painter = painterResource(Res.drawable.arrow_back_ios),
                                contentDescription = "Назад",
                                tint = Color.Black,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black
                    )
                )
            },
            containerColor = Color(0xFFF5F5F5)
        ) { padding ->

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {

                    // ====== ШАПКА ЧЕКА ======
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF00D4D4))
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Column {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = tx.dateNoSeconds(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // ====== ИТОГО ======
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Сумма",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Text(
                                text = tx.amountUi().text,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Бейдж операции (ваш payTitle())
                        Surface(
                            modifier = Modifier.padding(start = 10.dp),
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center // Центрирует содержимое внутри Row
                            ) {
                                Text(
                                    text = tx.payTitle(),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }


                        }
                    }

                    Divider(color = Color(0xFFE6E6E6), thickness = 1.dp)

                    // ====== ДЕТАЛИ (поле : значение) ======
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {

                        ReceiptRow(label = "ID транзакции", value = tx.idTransaction.toString())
                        ReceiptDivider()

                        ReceiptRow(label = "Тип топлива", value = fuelType)
                        ReceiptDivider()
                        ReceiptRow(
                            label = "Цена топлива",
                            value = if (fuelPrice == "—") "—" else "$fuelPrice cмн/л"
                        )
                        ReceiptDivider()

                        ReceiptRow(
                            label = "Литр",
                            value = if (liters == "—") "—" else "$liters л"
                        )
                        ReceiptDivider()

                        ReceiptRow(label = "Бонус по операции", value = bonusOp)
                        ReceiptDivider()

                        ReceiptRow(label = "Бонус (всего)", value = bonusTotal)
                        ReceiptDivider()

                        ReceiptRow(label = "Дата", value = tx.dateNoSeconds())
                    }
                }
            }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.height(0.dp))

        Text(
            text = value.ifBlank { "—" },
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ReceiptDivider() {
    Spacer(Modifier.height(10.dp))
    Divider(color = Color(0xFFE6E6E6), thickness = 1.dp)
    Spacer(Modifier.height(10.dp))
}

/**
 * Небольшие helpers, чтобы не падать на null/пустых строках
 * и красиво показывать прочерки/нули.
 */
private fun String?.orDash(): String = this?.takeIf { it.isNotBlank() } ?: "—"
private fun String?.orZero(): String = this?.takeIf { it.isNotBlank() } ?: "0"