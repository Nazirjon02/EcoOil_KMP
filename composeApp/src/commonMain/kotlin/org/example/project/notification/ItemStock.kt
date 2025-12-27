package org.example.project.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.example.data.Stock
import org.example.project.home.ShimmerPlaceholder
import org.example.project.home.StockDetailsScreen

@Composable
fun StockCardClean(
    stock: Stock,
    onClick: () -> Unit
) {

    val navigator = LocalNavigator.currentOrThrow
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                navigator.push(StockDetailsScreen(stock))
            })
    ) {

        Column {

            // ===== КАРТИНКА =====
            KamelImage(
                resource = asyncPainterResource(
                    "http://95.142.86.183:8080/images/stock/${stock.stock_image}"
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp),

                onLoading = {
                    ShimmerPlaceholder(
                        Modifier
                            .fillMaxWidth()
                            .height(190.dp)
                    )
                },

                onFailure = {
                    ShimmerPlaceholder(
                        Modifier
                            .fillMaxWidth()
                            .height(190.dp)
                    )
                }
            )

            // ===== ТЕКСТОВЫЙ БЛОК =====
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = stock.stock_title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    color = Color.Black
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = stock.stock_date_time,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
