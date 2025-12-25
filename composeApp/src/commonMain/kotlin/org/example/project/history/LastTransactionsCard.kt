package org.example.project.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ecooil_kmp.composeapp.generated.resources.Res
import ecooil_kmp.composeapp.generated.resources.ic_ai92
import ecooil_kmp.composeapp.generated.resources.ic_ai95
import ecooil_kmp.composeapp.generated.resources.ic_dt
import ecooil_kmp.composeapp.generated.resources.ic_dtecto
import ecooil_kmp.composeapp.generated.resources.ic_gas
import org.example.data.TransactionDto
import org.example.data.toDoubleSafe
import org.jetbrains.compose.resources.painterResource


import androidx.compose.foundation.layout.*
import androidx.compose.ui.text.style.TextOverflow
import ecooil_kmp.composeapp.generated.resources.icon_shop
import org.example.data.amountUi
import org.example.data.bonusText
import org.example.data.dateNoSeconds
import org.example.data.oilSizeCompleted
import org.example.data.payTitle
import org.example.data.productName
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LastTransactionsCard(
    transactions: List<TransactionDto>,
    onOpenTransactions: () -> Unit,
    onOpenTransactionDetails: (TransactionDto) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 20.dp)
            .clickable {
                onOpenTransactions()
                       },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Последние операции",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Все",
                    fontSize = 14.sp,
                    color = Color(0xFF1E88E5)
                )
            }

            Spacer(Modifier.height(12.dp))

            val items = transactions.take(4)
            if (items.isEmpty()) {
                Text("Операций пока нет", color = Color.Gray, fontSize = 13.sp)
            } else {
                items.forEachIndexed { index, tx ->
                    TransactionRow(
                        tx = tx,
                        onClick = { onOpenTransactionDetails(tx) }
                    )
                    if (index != items.lastIndex) Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
@Preview
fun TransactionRow(
    tx: TransactionDto,
    onClick: () -> Unit
) {
    val amount = tx.amountUi()
    val amountColor = if (amount.isDebit) Color(0xFFC62828) else Color(0xFF2E7D32)

    val title = tx.payTitle() // pay_name или по pay_status
    val subtitle = "${tx.productName()} • ${tx.oilSizeCompleted()}"
    val bonus = tx.bonusText()

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {

                val iconRes = resolveIcon(tx)
Card(shape = RoundedCornerShape(45),
colors = CardDefaults.cardColors(Color(0xFFE3F2FD))) {
    Icon(
        painter = painterResource(iconRes),
        contentDescription = null,
        tint = Color.Unspecified ,
        modifier = Modifier.size(45.dp).padding(7.dp)

    )
}

Spacer(Modifier.width(12.dp))

Column(modifier = Modifier.weight(1f).padding(end = 3.dp)) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )

    Spacer(Modifier.height(4.dp))

    Text(
        text = subtitle,
        fontSize = 12.sp,
        color = Color.Gray,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}
}

// Правая часть фиксированной ширины, чтобы длинный title не ломал суммы
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.widthIn(min = 100.dp, max = 160.dp)
            ) {
                // ✅ payStatus == 1: показываем только бонус, без суммы
                if (tx.pipSizeCompleted.toDoubleSafe() > 0 ) {

                    Text(
                        text = tx.bonusText(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2E7D32),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(3.5.dp))

                    Text(
                        text = amount.text,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = amountColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )


                    Spacer(Modifier.height(3.5.dp))

                    Text(
                        text = tx.dateNoSeconds(),
                        fontSize = 11.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Text(
                        text = amount.text,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = amountColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = tx.dateNoSeconds(),
                        fontSize = 11.5.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }}
}
}
}

private fun resolveIcon(tx: TransactionDto): DrawableResource {
    val t = tx.oilTypeName?.trim().orEmpty()


    if (t == "0" || t.isEmpty()) return Res.drawable.icon_shop

    return when (t.uppercase()) {
        "АИ-95" -> Res.drawable.ic_ai95
        "АИ-92" -> Res.drawable.ic_ai92
        "ДТ" -> Res.drawable.ic_dt
        "ДТ-ЭКТО", "ДТ-ЭКТО" -> Res.drawable.ic_dtecto
        "ГАЗ" -> Res.drawable.ic_gas
        else -> Res.drawable.ic_ai92
    }
}




