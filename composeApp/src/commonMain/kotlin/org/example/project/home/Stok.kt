package org.example.project.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.example.data.Stock
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ecooil_kmp.composeapp.generated.resources.Res
import ecooil_kmp.composeapp.generated.resources.arrow_back_ios

import org.jetbrains.compose.resources.painterResource


@Composable
fun StockImageCard(
    item: Stock,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .size(80.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        KamelImage(
            resource = asyncPainterResource(
                "http://95.142.86.183:8080/images/stock/" + item.stock_image
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),

            onLoading = {
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
            },

            onFailure = {
                // ❗️ если нет интернета — просто продолжаем shimmer
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
            }
        )
    }
}
class StockDetailsScreen(
    private val stock: Stock,
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Акция",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick ={
                            navigator.pop()
                        }) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                painter = painterResource(Res.drawable.arrow_back_ios),
                                contentDescription = "Назад"
                            )
                        }
                    }
                )
            },
            containerColor = Color(0xFFF6F6F6)
        ) { padding ->

            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ===== КАРТИНКА (Hero Image) =====
                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    KamelImage(
                        resource = asyncPainterResource(
                            "http://95.142.86.183:8080/images/stock/" + stock.stock_image
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),

                        onLoading = {
                            ShimmerPlaceholder(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                            )
                        },

                        onFailure = {
                            // ❗️ если нет интернета — просто продолжаем shimmer
                            ShimmerPlaceholder(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                            )
                        }
                    )
                }

                Spacer(Modifier.height(20.dp))

                // ===== КОНТЕНТ =====
                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        // Заголовок
                        Text(
                            text = stock.stock_title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )

                        Spacer(Modifier.height(10.dp))

                        // Дата
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Дата:",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = stock.stock_date_time,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

                        Spacer(Modifier.height(16.dp))
                        // Описание-заглушка (если потом появится текст акции)
                        Text(
                            text = "Подробная информация об акции. Следите за обновлениями и используйте выгодные предложения в приложении.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF444444),
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ShimmerPlaceholder(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "shimmer")

    val translateX by transition.animateFloat(
        initialValue = -300f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            )
        ),
        label = "shimmerAnim"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFE0E0E0),
            Color(0xFFF5F5F5),
            Color(0xFFE0E0E0)
        ),
        start =
            Offset(translateX, 0f),
        end = Offset(translateX + 300f, 0f)
    )

    Box(
        modifier = modifier
            .background(brush)
    )
}