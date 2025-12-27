package org.example.project.notification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.data.TransactionDto
import org.example.data.amountUi
import org.example.data.buildBonusText
import org.example.data.dateNoSeconds
import org.example.data.orDash
import org.example.data.payTitle
import org.example.data.productName

@Composable
fun NotificationItem(tx: TransactionDto) {

    val amount = tx.amountUi()

    Card(
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            // ===== Верх =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = tx.payTitle(),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                Text(
                    text = amount.text,
                    fontWeight = FontWeight.Bold,
                    color = if (amount.isDebit) Color(0xFFD32F2F) else Color(0xFF388E3C)
                )
            }

            Spacer(Modifier.height(6.dp))

            // ===== Дата =====
            Text(
                text = tx.dateNoSeconds(),
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(8.dp))

            // ===== Детали =====
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Text(
                    text = "Станция: ${tx.oilStationName!!.orDash()}",
                    fontSize = 13.sp
                )

                Text(
                    text = "Транзакция: ${tx.productName()}",
                    fontSize = 13.sp
                )

                Text(
                    text = buildBonusText(tx),
                    fontSize = 13.sp,
                    color = Color(0xFF6A1B9A)
                )
            }
        }
    }
}
