package org.example.data

import ecooil_kmp.composeapp.generated.resources.Res
import ecooil_kmp.composeapp.generated.resources.ic_ai92
import ecooil_kmp.composeapp.generated.resources.ic_ai95
import ecooil_kmp.composeapp.generated.resources.ic_dt
import ecooil_kmp.composeapp.generated.resources.ic_dtecto
import ecooil_kmp.composeapp.generated.resources.ic_gas
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource

@Serializable
data class CarResponse(
    val code: Int,
    val message: String,
    val data: CarDataWrapper? = null

)

@Serializable
data class CarDataWrapper(
    val car_data: CarData? = null,
    val car_oil: CarOil? = null
)

@Serializable
data class CarData(
    val company_name: String,
    val car_status: Int,
    val car_date_time_registration: String,
    val card_number: String,
    val car_name: String,
    val car_number: String,
    val car_phone_number: String,
    val car_birth_date_time: String,
    val car_gender: Int,
    val pip_status_type: String,
    val car_pip_size: Double,
    val car_balance_size: Int
)

@Serializable
data class CarOil(
    val ai95: String,
    val ai92: String,
    val dt: String,
    val gas: String,
    val dtecto: String
)
data class FuelItem(
    val title: String,
    val value: String,
    val icon: DrawableResource
)

@Serializable
data class FuelItemCache(
    val type: FuelType,
    val price: String
)

enum class FuelType {
    AI95,
    AI92,
    DT,
    DTECTO,
    GAS
}
fun FuelType.icon(): DrawableResource =
    when (this) {
        FuelType.AI95 -> Res.drawable.ic_ai95
        FuelType.AI92 -> Res.drawable.ic_ai92
        FuelType.DT -> Res.drawable.ic_dt
        FuelType.DTECTO -> Res.drawable.ic_dtecto
        FuelType.GAS -> Res.drawable.ic_gas
    }

fun FuelType.title(): String =
    when (this) {
        FuelType.AI95 -> "АИ-95"
        FuelType.AI92 -> "АИ-92"
        FuelType.DT -> "ДТ"
        FuelType.DTECTO -> "ДТ-Экто"
        FuelType.GAS -> "ГАЗ"
    }

data class HomeCache(
    val userName: String,
    val bonusText: String,
    val balanceText: String,
    val statusText: String,
    val fuelItems: List<FuelItem>
)
