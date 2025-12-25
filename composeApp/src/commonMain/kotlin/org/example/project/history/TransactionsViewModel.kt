package org.example.project.history

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.example.data.TransactionDto
import org.example.data.TransactionsResponse
import org.example.networking.Constant
import org.example.networking.InsultCensorClient
import org.example.util.AppSettings
import org.example.util.NetworkError
import org.example.util.onError
import org.example.util.onSuccess

class TransactionsScreenModel : ScreenModel {

    private val _transactions = mutableStateListOf<TransactionDto>()
    val transactions: List<TransactionDto> get() = _transactions

    var allLoaded = mutableStateOf(false)
    private var currentLimit = 15
    var isLoading = mutableStateOf(false)

    fun loadTransactions(client: InsultCensorClient?) {
        if (client == null) return
        if (isLoading.value || allLoaded.value) return

        isLoading.value = true
        screenModelScope.launch {
            try {
                val result = getTransactions(client, currentLimit) // используйте вашу функцию из файла
                when (result) {
                    is org.example.data.ApiCallResult.Success -> {
                        val list = result.body.data.listTransaction
                        _transactions.addAll(list)
                        if (list.size < 15) allLoaded.value = true
                        currentLimit += 15
                    }
                    is org.example.data.ApiCallResult.Failure -> {
                        // при необходимости лог/ошибка
                    }
                }
            } finally {
                isLoading.value = false
            }
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