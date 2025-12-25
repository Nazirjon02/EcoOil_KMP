package org.example.project.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ecooil_kmp.composeapp.generated.resources.Res
import ecooil_kmp.composeapp.generated.resources.ic_ai92
import ecooil_kmp.composeapp.generated.resources.ic_ai95
import ecooil_kmp.composeapp.generated.resources.ic_dt
import ecooil_kmp.composeapp.generated.resources.ic_dtecto
import ecooil_kmp.composeapp.generated.resources.ic_gas
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.data.ApiCallResult
import org.example.data.FuelItem
import org.example.data.FuelItemCache
import org.example.data.FuelType
import org.example.data.HomeCache
import org.example.data.ResponseStock
import org.example.data.Stock
import org.example.data.TransactionDto
import org.example.data.icon
import org.example.data.title
import org.example.networking.Constant
import org.example.networking.Constant.InvalidToken
import org.example.networking.InsultCensorClient
import org.example.project.history.getTransactions
import org.example.util.AppSettings
import org.example.util.AppSettings.loadStocksFromCache
import org.example.util.AppSettings.loadTransactionsFromCache
import org.example.util.AppSettings.saveStocksToCache
import org.example.util.AppSettings.saveTransactionsToCache
import org.example.util.NetworkError
import org.example.util.onError
import org.example.util.onSuccess

class HomeViewModel(
    private val client: InsultCensorClient?
) : ViewModel() {
    var transactions by mutableStateOf<List<TransactionDto>>(emptyList())
        private set

    var stocks by mutableStateOf<List<Stock>>(emptyList())
        private set

    var userName by mutableStateOf("Test")
        private set

    var bonusText by mutableStateOf("0.00 c")
        private set

    var balanceText by mutableStateOf("0.00 c")
        private set

    var statusText by mutableStateOf("Status / SG Lite")
        private set

    var fuelItems by mutableStateOf<List<FuelItem>>(emptyList())
        private set

    var tokenError by mutableStateOf(false)
        private set

    var isRefreshMainScreen by mutableStateOf(false)
        private set

    private var cache: HomeCache? = null

    private val isFirstLaunch: Boolean
        get() = AppSettings.getBoolean("isFirstLaunch", true)

    init {
        loadDataFromSettings()
        transactions = loadTransactionsFromCache().take(4)

        // Если это первый запуск, делаем запрос на сервер
        if (isFirstLaunch) {
            loadFromNetwork(isFirstLaunch = true)
        } else if (fuelItems.isEmpty()) {
            loadFromNetwork(isFirstLaunch = false)
        }

        stocks = loadStocksFromCache()
    }

    private fun loadDataFromSettings() {
        val savedUserName = AppSettings.getString("car_name", "")
        val savedBonusText = AppSettings.getString("car_pip_size", "0.00 C")
        val savedBalanceText = AppSettings.getString("car_balance_size", "0.00 C")
        val savedStatusText = AppSettings.getString("pip_status_type", "Status / SG Lite")

        if (savedUserName.isNotEmpty()) {
            // Если данные есть в настройках — используем их
            userName = savedUserName
            bonusText = savedBonusText
            balanceText = savedBalanceText
            statusText = savedStatusText

            // Пример загрузки топлива из AppSettings
            fuelItems = loadFuelItemsFromSettings()
        }
    }

    // Метод для загрузки данных с сервера
    private fun loadFromNetwork(isFirstLaunch: Boolean) {
        // Проверяем, не загружены ли уже данные
        if (isRefreshMainScreen) return

        isRefreshMainScreen = true

        viewModelScope.launch {

            requestStocks(
                client = client,
                onSuccess = { body ->
                    val list = body.data.list_stock
                    stocks = list
                    saveStocksToCache(list)
                },
                onError = {
                    // если сеть упала — оставляем кэш (stocks уже загружен из loadStocksFromCache())
                }
            )



            requestCarData(
                client = client,
                onSuccess = { body ->
                    if (body.message == InvalidToken) {
                        tokenError = true
                        return@requestCarData
                    }

                    val newCache = HomeCache(
                        userName = body.data?.car_data?.car_name ?: userName,
                        bonusText = "${body.data?.car_data?.car_pip_size ?: 0} C",
                        balanceText = "${body.data?.car_data?.car_balance_size ?: 0} C",
                        statusText = body.data?.car_data?.pip_status_type.toString(),
                        fuelItems = body.data?.car_oil?.let { oil ->
                            listOf(
                                FuelItem("АИ-95", "${oil.ai95} cмн", Res.drawable.ic_ai95),
                                FuelItem("АИ-92", "${oil.ai92} cмн", Res.drawable.ic_ai92),
                                FuelItem("ДТ", "${oil.dt} cмн", Res.drawable.ic_dt),
                                FuelItem("ДТ-Экто", "${oil.dtecto} cмн", Res.drawable.ic_dtecto),
                                FuelItem("ГАЗ", "${oil.gas} cмн", Res.drawable.ic_gas)
                            )
                        } ?: emptyList()
                    )

                    // Сохраняем данные в AppSettings
                    AppSettings.putString("car_name", body.data?.car_data?.car_name.toString())
                    AppSettings.putString("car_number", body.data?.car_data?.car_number.toString())
                    AppSettings.putString("card_number", body.data?.car_data?.card_number.toString())
                    AppSettings.putString("car_pip_size", body.data?.car_data?.car_pip_size.toString())
                    AppSettings.putString("car_balance_size", body.data?.car_data?.car_balance_size.toString())
                    AppSettings.putString("car_phone_number", body.data?.car_data?.car_phone_number.toString())
                    AppSettings.putString("pip_status_type", body.data?.car_data?.pip_status_type.toString())
                    AppSettings.putString("car_birth_date_time", body.data?.car_data?.car_birth_date_time.toString())

                    // Сохраняем, что это больше не первый запуск
                    if (isFirstLaunch) {
                        AppSettings.putBoolean("isFirstLaunch", false)
                    }

                    if (fuelItems.isNotEmpty()) {
                        fuelItems = fuelItems
                        saveFuelItems(fuelItems) // ✅ сохраняем
                    }

                    // Кэшируем данные
                    cache = newCache
                    applyCache(newCache)
                },
                onError = {
                    when (it){
                        NetworkError.UNKNOWN ->{
                            //Toast нет интернета
                        }
                        NetworkError.NO_INTERNET -> {
                            //Toast нет интернета
                        }
                        else -> {}
                    }
                    isRefreshMainScreen = false

                }
            )

            val txResult = getTransactions(client,4)

            when (txResult) {
                is ApiCallResult.Success -> {
                    val items = txResult.body.data.listTransaction
                        .take(4) // гарантируем 4
                    transactions = items
                    saveTransactionsToCache(items)
                }
                is ApiCallResult.Failure -> {
                    // если сеть упала — оставляем кеш (уже загружен в init)
                    // при желании можешь показать toast
                }
            }

            isRefreshMainScreen = false
        }
    }

    // Обновление данных
    fun refresh() {
        loadFromNetwork(isFirstLaunch = false)
    }

    private fun applyCache(cache: HomeCache) {
        userName = cache.userName
        bonusText = cache.bonusText
        balanceText = cache.balanceText
        statusText = cache.statusText
        fuelItems = cache.fuelItems
    }
}

private fun saveFuelItems(items: List<FuelItem>) {
    val cache = items.map {
        FuelItemCache(
            type = when (it.title) {
                "АИ-95" -> FuelType.AI95
                "АИ-92" -> FuelType.AI92
                "ДТ" -> FuelType.DT
                "ДТ-Экто" -> FuelType.DTECTO
                else -> FuelType.GAS
            },
            price = it.value
        )
    }

    val json = Json.encodeToString(cache)
    AppSettings.putString("fuel_items", json)
}

private fun loadFuelItemsFromSettings(): List<FuelItem> {
    val json = AppSettings.getString("fuel_items", "")
    if (json.isEmpty()) return emptyList()

    return try {
        val cache = Json.decodeFromString<List<FuelItemCache>>(json)
        cache.map {
            FuelItem(
                title = it.type.title(),
                value = it.price,
                icon = it.type.icon()
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}



suspend fun requestStocks(
    client: InsultCensorClient?,
    onSuccess: (ResponseStock) -> Unit,
    onError: (NetworkError?) -> Unit
) {
    try {
        val hash = org.example.project.Until.sha256(
            AppSettings.getInt("car_id").toString() +
                    AppSettings.getString("token") +
                    org.example.project.Until.getDeviceId()
        )

        val map = hashMapOf(
            "Token" to AppSettings.getString("token"),
            "DeviceId" to org.example.project.Until.getDeviceId(),
            "CarId" to AppSettings.getInt("car_id").toString(),
            "Limit" to 8,
            "Hash" to hash
        )

        val result = client?.request<ResponseStock>(
            path = Constant.getStocks,
            params = map,
        )

        result?.onSuccess { body ->
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




