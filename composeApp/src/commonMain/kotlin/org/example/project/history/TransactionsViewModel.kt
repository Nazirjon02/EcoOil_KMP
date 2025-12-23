package org.example.project.history

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.navigator.LocalNavigator
import kotlinx.coroutines.launch
import org.example.data.ApiCallResult
import org.example.data.TransactionDto
import org.example.data.dateNoSeconds
import org.example.data.dateOnly
import org.example.networking.InsultCensorClient
import org.example.project.home.TransactionRow

class TransactionsViewModel : ViewModel() {
    private val _transactions = mutableStateListOf<TransactionDto>()
    val transactions: List<TransactionDto> get() = _transactions

    private var currentLimit = 15
    var isLoading = mutableStateOf(false)

    // Функция для загрузки данных
    fun loadTransactions(client: InsultCensorClient?) {
        if (isLoading.value) return  // Если данные уже загружаются, не делать новый запрос

        isLoading.value = true
        viewModelScope.launch {
            val result = getTransactions(client, currentLimit)
            when (result) {
                is ApiCallResult.Success -> {
                    _transactions.addAll(result.body.data.listTransaction ?: emptyList())
                    currentLimit += 15  // Увеличиваем лимит на 15 для следующего запроса
                }
                is ApiCallResult.Failure -> {
                    // Обработать ошибку, если необходимо
                }
            }
            isLoading.value = false
        }
    }
}


@Composable
fun LazyColumnWithPagination(
    transactions: List<TransactionDto>,
    onLoadMore: () -> Unit,  // Вызывается для подгрузки данных
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Группируем транзакции по дате
        transactions.groupBy { it.dateNoSeconds().dateOnly() }.forEach { (date, txList) ->
            item {
                DateHeader(date)
            }
            items(txList) { tx ->
                TransactionRow(tx = tx, onClick = {
                    // Переход на экран с деталями транзакции
                    //LocalNavigator.current?.push(TransactionDetailsScreen(tx))
                })
            }
        }

        // Показ подгрузки данных при прокрутке
        item {
            if (listState.firstVisibleItemIndex + listState.layoutInfo.visibleItemsInfo.size >= transactions.size) {
                onLoadMore()  // Если прокручено до конца — подгрузить новые данные
            }
        }
    }
}


