package org.example.project.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import kotlinx.datetime.LocalDateTime
import org.example.data.ApiCallResult
import org.example.data.TransactionDto
import org.example.data.TransactionsResponse
import org.example.data.amountUi

import org.example.data.payTitle
import org.example.networking.Constant
import org.example.networking.InsultCensorClient
import org.example.project.MainRootScreen
import org.example.project.home.TransactionDetailsScreen
import org.example.project.home.TransactionRow
import org.example.util.AppSettings
import org.example.util.onError
import org.example.util.onSuccess
class TransactionsScreenParent(
    private val client: InsultCensorClient
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel: TransactionsViewModel = viewModel()

        // Убираем remember, чтобы UI обновлялся при изменении списка
        val transactions = viewModel.transactions

        LaunchedEffect(Unit) {
            viewModel.loadTransactions(client)
        }
        TransactionsScreen(
            client = client,
            transactions = transactions,
            viewModel = viewModel
        )
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(client: InsultCensorClient?, transactions: List<TransactionDto>, viewModel: TransactionsViewModel) {
    val groupedTransactions = groupTransactionsByDate(transactions)
    val listState = rememberLazyListState()


    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Операции") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                groupedTransactions.forEach { (date, transactionsList) ->
                    item { DateHeader(date) }
                    items(transactionsList) { tx ->
                        TransactionRow(
                            tx = tx,
                            onClick = {
                                // Навигация на экран с деталями транзакции
                                //LocalNavigator.current?.push(TransactionDetailsScreen(tx))
                            }
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }

                // Показать индикатор загрузки в конце списка
                if (viewModel.isLoading.value) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
            }
        }

        // Пагинация при скролле до конца
        LaunchedEffect(listState) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                .collect { lastVisible ->
                    val totalItems = listState.layoutInfo.totalItemsCount
                    if (listState.layoutInfo.totalItemsCount - listState.firstVisibleItemIndex < 5) {
                        viewModel.loadTransactions(client)
                    }

                }
        }
    }
}


data class TransactionDto(val dateTime: String?)

fun TransactionDto.toLocalDateTime(): LocalDateTime? {
    return try {
        // Преобразуем "dd.MM.yyyy HH:mm:ss" в формат ISO для LocalDateTime
        dateTime?.let { dt ->
            val parts = dt.split(" ", ":",".")  // ["21","12","2025","17","53","11"]
            if (parts.size >= 6) {
                LocalDateTime(
                    year = parts[2].toInt(),
                    monthNumber = parts[1].toInt(),
                    dayOfMonth = parts[0].toInt(),
                    hour = parts[3].toInt(),
                    minute = parts[4].toInt(),
                    second = parts[5].toInt()
                )
            } else null
        }
    } catch (e: Exception) {
        println("Failed to parse: $dateTime")
        null
    }
}


fun TransactionDto.dateOnly(): String {
    return dateTime?.substringBefore(" ") ?: "-"
}

fun groupTransactionsByDate(list: List<TransactionDto>): Map<String, List<TransactionDto>> {
    val sorted = list.sortedByDescending { it.toLocalDateTime() }
    val grouped = sorted.groupBy { it.dateOnly() }
    return grouped
}



fun kotlinx.datetime.LocalDate.toUi(): String {
    return "${dayOfMonth}.${monthNumber}.${year}"
}

@Composable
fun DateHeader(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.Gray,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}




suspend fun getTransactions(client: InsultCensorClient?,limit:Int): ApiCallResult<TransactionsResponse> {
    return try {
        if (client == null) return ApiCallResult.Failure(message = "Client is null")

        val hash = org.example.project.Until.sha256(
            org.example.project.Until.getDeviceId() +
                    AppSettings.getString("token") +
                    AppSettings.getInt("car_id").toString()
        )

        val map = hashMapOf(
            "Token" to AppSettings.getString("token"),
            "DeviceId" to org.example.project.Until.getDeviceId(),
            "CarId" to AppSettings.getInt("car_id").toString(),
            "Limit" to limit,
            "Hash" to hash
        )

        val result = client.request<TransactionsResponse>(
            path = Constant.getTransactions,
            params = map
        )

        var out: ApiCallResult<TransactionsResponse> = ApiCallResult.Failure(message = "Unknown")

        result
            .onSuccess { body ->
                out = if (body.code == 1) ApiCallResult.Success(body)
                else ApiCallResult.Failure(message = body.message)
            }
            .onError { e ->
                out = ApiCallResult.Failure(error = null)
            }

        out
    } catch (e: Throwable) {
        ApiCallResult.Failure(error = e)
    }
}