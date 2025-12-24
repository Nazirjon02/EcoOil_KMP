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
import org.example.data.CarResponse
import org.example.data.TransactionDto
import org.example.data.TransactionsResponse

import org.example.networking.Constant
import org.example.networking.InsultCensorClient
import org.example.project.home.TransactionRow
import org.example.util.AppSettings
import org.example.util.NetworkError
import org.example.util.onError
import org.example.util.onSuccess

class TransactionsViewModel : ViewModel() {
    private val _transactions = mutableStateListOf<TransactionDto>()
    val transactions: List<TransactionDto> get() = _transactions
    var allLoaded = mutableStateOf(false)


    private var currentLimit = 15
    var isLoading = mutableStateOf(false)

    // Функция для загрузки данных
    fun loadTransactions(client: InsultCensorClient?) {
        if (isLoading.value || allLoaded.value) return

        isLoading.value = true
        viewModelScope.launch {
            getTransactionsData(
                client = client,
                limit = currentLimit,
                onSuccess = { body ->
                    _transactions.addAll(body.data.listTransaction)
                    if (body.data.listTransaction.size < 15) allLoaded.value = true
                    currentLimit += 15  // Увеличиваем лимит на 15 для следующего запроса
                },
                onError = {

                },
            )

                    isLoading.value = false
                }
    }
}



suspend fun getTransactionsData(
    client: InsultCensorClient?,
    limit: Int,
    onSuccess: (TransactionsResponse) -> Unit,
    onError: (NetworkError?) -> Unit
) {
    try {
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


        val result = client!!.request<TransactionsResponse>(
            path = Constant.getTransactions,
            params = map
        )


        result.onSuccess { body ->
            onSuccess(body)
        }?.onError { e ->
            onError(e)
        } ?: run {
            onError(null)
        }
    } catch (e: Throwable) {
        onError(null)
    }
}