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
import org.example.data.FuelItem
import org.example.networking.Constant.isLoaded
import org.example.networking.InsultCensorClient
import org.example.util.AppSettings

class HomeViewModel(
    private val client: InsultCensorClient?
) : ViewModel() {

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


    private var cache: HomeCache? = null   // 🔹 КЕШ

    init {
        load()
    }

    /** Автоматическая загрузка */
    private fun load() {
        // Если есть кеш — просто используем его
        cache?.let {
            applyCache(it)
            return
        }

        loadFromNetwork()
    }

    /** Принудительное обновление */
    fun refresh() {
        loadFromNetwork()
    }

    private fun loadFromNetwork() {
        if (isLoaded) return

        isLoaded = true

        viewModelScope.launch {
            requestCarData(
                client = client,
                onSuccess = { body ->
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

                    AppSettings.putString("car_name",body.data?.car_data?.car_name.toString())
                    AppSettings.putString("car_number",body.data?.car_data?.car_number.toString())
                    AppSettings.putString("car_phone_number",body.data?.car_data?.car_phone_number.toString())
                    AppSettings.putString("pip_status_type",body.data?.car_data?.pip_status_type.toString())
                    AppSettings.putString("car_birth_date_time",body.data?.car_data?.car_birth_date_time.toString())


                    cache = newCache
                    applyCache(newCache)
                },
                onError = {}
            )
            isLoaded = false
        }
    }

    private fun applyCache(cache: HomeCache) {
        userName = cache.userName
        bonusText = cache.bonusText
        balanceText = cache.balanceText
        statusText = cache.statusText
        fuelItems = cache.fuelItems
    }
}
data class HomeCache(
    val userName: String,
    val bonusText: String,
    val balanceText: String,
    val statusText: String,
    val fuelItems: List<FuelItem>
)
