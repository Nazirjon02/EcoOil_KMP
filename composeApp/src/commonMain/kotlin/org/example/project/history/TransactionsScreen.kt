package org.example.project.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ecooil_kmp.composeapp.generated.resources.Res
import ecooil_kmp.composeapp.generated.resources.arrow_back_ios
import kotlinx.datetime.LocalDateTime
import org.example.data.ApiCallResult
import org.example.data.TransactionDto
import org.example.data.TransactionsResponse
import org.example.networking.Constant
import org.example.networking.InsultCensorClient
import org.example.util.AppSettings
import org.example.util.onError
import org.example.util.onSuccess
import org.jetbrains.compose.resources.painterResource

class TransactionsScreenParent(
    private val client: InsultCensorClient
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        // val viewModel: TransactionsViewModel = remember { TransactionsViewModel() }
        val screenModel = rememberScreenModel { TransactionsScreenModel() }
        val transactions = screenModel.transactions
        // Убираем remember, чтобы UI обновлялся при изменении списка

        LaunchedEffect(Unit) {
            screenModel.loadTransactions(client)
        }

        TransactionsScreen(
            client = client,
            transactions = transactions,
            viewModel = screenModel,
            navigator = navigator,
            onBack = {
                navigator.pop()

            }
        )
    }


}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun TransactionsScreen(
    client: InsultCensorClient?,
    transactions: List<TransactionDto>,
    viewModel: TransactionsScreenModel,
    navigator: Navigator,
    onBack: () -> Unit  // Лямбда для кнопки назад
) {

    BackHandler(enabled = true) {
        onBack()
    }

    val groupedTransactions = groupTransactionsByDate(transactions)
    val listState = rememberLazyListState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Операции") },

                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(Res.drawable.arrow_back_ios)
                            , contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            if (transactions.isEmpty() && !viewModel.isLoading.value) {
                // История пустая
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Пока что история пуста 😕", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().background(Color.White),
                    contentPadding = PaddingValues(bottom = 16.dp, start = 16.dp, end = 16.dp, top = 10.dp)
                ) {
                    groupedTransactions.forEach { (date, transactionsList) ->
                        item { DateHeader(date) }
                        items(transactionsList) { tx ->
                            TransactionRow(tx = tx,
                                onClick = { // Навигация на экран с деталями транзакции
                                   navigator.push(TransactionDetailsScreen(tx))
                                }
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                    }

                    // Показать индикатор загрузки в конце списка
                    if (viewModel.isLoading.value) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }

                // Пагинация при скролле до конца
                LaunchedEffect(listState) {
                    snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                        .collect { lastVisible ->
                            val totalItems = listState.layoutInfo.totalItemsCount
                            if (totalItems - listState.firstVisibleItemIndex < 5) {
                                viewModel.loadTransactions(client)
                            }
                        }
                }
            }
        }
    }
}




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