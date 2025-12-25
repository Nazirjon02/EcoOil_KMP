package org.example.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionsResponse(
    val code: Int,
    val message: String,
    val data: TransactionsData
)

@Serializable
data class TransactionsData(
    @SerialName("list_transaction")
    val listTransaction: List<TransactionDto>
)

@Serializable
data class TransactionDto(

    @SerialName("id_transaction")
    val idTransaction: String? = null,

    @SerialName("oil_station_name")
    val oilStationName: String? = null,

    @SerialName("date_time")
    val dateTime: String? = null,

    @SerialName("oil_type_name")
    val oilTypeName: String? = null,

    @SerialName("oil_price")
    val oilPrice: String? = null,

    @SerialName("trk_id")
    val trkId: Int? = null,

    @SerialName("pip_status_type")
    val pipStatusType: String? = null,

    @SerialName("oil_size_started")
    val oilSizeStarted: String? = null,

    @SerialName("oil_size_completed")
    val oilSizeCompleted: String? = null,

    @SerialName("oil_summa_started")
    val oilSummaStarted: String? = null,

    @SerialName("oil_summa_completed")
    val oilSummaCompleted: String? = null,

    @SerialName("pip_size_started")
    val pipSizeStarted: String? = null,

    @SerialName("pip_size_completed")
    val pipSizeCompleted: String? = null,

    @SerialName("take_pip")
    val takePip: String? = null,

    @SerialName("take_balance")
    val takeBalance: String? = null,

    @SerialName("return_status")
    val returnStatus: Int? = null,

    @SerialName("pay_status")
    val payStatus: Int? = null,

    // если сервер реально отдаёт pay_name — отлично.
    // если не отдаёт — поле будет null, и мы будем строить текст по pay_status.
    @SerialName("pay_name")
    val payName: String? = null,

    @SerialName("car_pip_size")
    val carPipSize: String? = null
)
data class TransactionUiModel(
    val id: Long,
    val station: String,
    val dateTime: String,
    val oilType: String,
    val price: Double,
    val volume: Double,
    val amount: Double,
    val takenFromPip: Double,
    val takenFromBalance: Double,
    val pipStatus: String,
    val payStatus: Int,
    val payName: String,
    val returnStatus: Int
)

//fun TransactionDto.toUi(): TransactionUiModel {
//    return TransactionUiModel(
//        id = idTransaction.toLong(),
//        station = oilStationName,
//        dateTime = dateTime,
//        oilType = oilTypeName,
//        price = oilPrice.toDoubleSafe(),
//        volume = oilSizeCompleted.toDoubleSafe(),
//        amount = oilSummaCompleted.toDoubleSafe(),
//        takenFromPip = takePip.toDoubleSafe(),
//        takenFromBalance = takeBalance.toDoubleSafe(),
//        pipStatus = pipStatusType,
//        payStatus = payStatus,
//        payName = payName,
//        returnStatus = returnStatus
//    )
//}

fun TransactionDto.productName(): String {
    val t = oilTypeName?.trim().orEmpty()
    return if (t == "0" || t.isEmpty()) "Магазин" else t
}

fun TransactionDto.oilSizeCompleted(): String {
    val t = oilSizeCompleted?.trim().orEmpty()
    return if (t == "0" || t.isEmpty()) "покупка" else "$t л"
}

fun TransactionDto.payTitle(): String {
    // Если сервер дал pay_name — используем его.
    val fromServer = payName?.trim()
    if (!fromServer.isNullOrEmpty()) return fromServer

    // Иначе строим по pay_status
    return when (payStatus ?: -1) {
        0 -> "Снятие бонусов"
        1 -> "Зачисление"
        2 -> "Снятие с баланса"
        3 -> "Снятие бонуса"
        4 -> "Снятие с баланса"
        else -> "Операция"
    }
}

@Serializable
data class TransactionsCache(
    val items: List<TransactionDto>
)


fun String.orDash(): String = if (isBlank()) "-" else this

sealed class ApiCallResult<out T> {
    data class Success<T>(val body: T) : ApiCallResult<T>()
    data class Failure(val error: Throwable? = null, val message: String? = null) : ApiCallResult<Nothing>()
}

fun TransactionDto.dateNoSeconds(): String {
    val raw = dateTime?.trim().orEmpty()
    return if (raw.length >= 16) raw.substring(0, 16) else raw.ifBlank { "-" }
}

fun String.dateOnly(): String {
    // "2024-12-22 14:35" -> "22.12.2024"
    return try {
        val date = substring(0, 10) // 2024-12-22
        val (y, m, d) = date.split("-")
        "$d.$m.$y"
    } catch (e: Exception) {
        this
    }
}


fun String?.toDoubleSafe(): Double =
    this?.replace(" ", "")?.replace(",", ".")?.toDoubleOrNull() ?: 0.0

fun formatMoneyKmp(value: Double): String {
    val rounded = kotlin.math.round(value * 100) / 100
    val parts = rounded.toString().split(".")
    val intPart = parts[0].reversed().chunked(3).joinToString(" ").reversed()
    val fracPart = parts.getOrNull(1)?.padEnd(2, '0') ?: "00"
    return "$intPart,$fracPart"
}

data class AmountUi(val text: String, val isDebit: Boolean)

fun TransactionDto.amountUi(): AmountUi {
    return when (payStatus ?: -1) {
        0, 3 -> { // списание бонуса
            val v = takePip.toDoubleSafe()
            AmountUi(text = "-${formatMoneyKmp(v)} смн", isDebit = true)
        }
        2, 4 -> { // списание баланса
            val v = takeBalance.toDoubleSafe()
            AmountUi(text = "-${formatMoneyKmp(v)} смн", isDebit = true)
        }
        1 -> { // зачисление
            val v = oilSummaCompleted.toDoubleSafe()
            AmountUi(text = "+${formatMoneyKmp(v)} смн", isDebit = false)
        }
        else -> {
            val v = oilSummaCompleted.toDoubleSafe()
            AmountUi(text = "${formatMoneyKmp(v)} смн", isDebit = false)
        }
    }
}

fun TransactionDto.bonusText(): String {
    val v = pipSizeCompleted.toDoubleSafe()
    return "Бонус: ${formatMoneyKmp(v)}"
}

 fun bonusOnlyText(tx: TransactionDto): String {
    val v = tx.pipSizeCompleted?.toDoubleSafe() ?: 0.0
    return "Бонус: ${formatMoneyKmp(v)}"
}
 fun buildBonusText(tx: TransactionDto): String {
    val bonus = tx.pipSizeCompleted.toDoubleSafe()
    return "Бонус: ${formatMoneyKmp(bonus)}"
}

val JsonRelaxed = kotlinx.serialization.json.Json {
    ignoreUnknownKeys = true
    isLenient = true
    encodeDefaults = true
}
//"id_transaction": "5",  id
//"oil_station_name": "Tes",   не нужно нужно толко для чек
//"date_time": "21.12.2025 17:53:11",  дата тайм
//"oil_type_name": "0",     топлифа  если 0 значит покупка из магазин
//"oil_price": "0",        цена топлива если 0 значит покупка из магазина
//"trk_id": 0,             калонка заправки если 0 значит мг
//"pip_status_type": "SG Full",         тариф клиента
//"oil_size_started": "0",
//"oil_size_completed": "0",
//"oil_summa_started": "0",
//"oil_summa_completed": "0",
//"pip_size_started": "0",
//"pip_size_completed": "0",
//"take_pip": "0",
//"take_balance": "200",
//"return_status": 0,
//"pay_status": 4,
//"car_pip_size": "20002,30",

//"id_transaction": "3",    не нужно это учитоват не нужно для клиента
//"oil_station_name": "Tes",   не нужно нужно толко для чек
//"date_time": "21.12.2025 17:50:37",   дата тайм
//"oil_type_name": "ДТ",      топлифа  если 0 значит покупка из магазин
//"oil_price": "5,3",       цена топлива если 0 значит покупка из магазина
//"trk_id": 3,                      калонка заправки если 0 значит мг
//"pip_status_type": "SG Full",     тариф клиента
//"oil_size_started": "100",       литр топлива
//"oil_size_completed": "33,34",   сколко клинет взял
//"oil_summa_started": "530",      первый сколко далклиент
//"oil_summa_completed": "176",     сколко клинет взял в итоге это на пример на полный бак
//"pip_size_started": "10",         бонус клиента
//"pip_size_completed": "3,33",     итогавая бонус
//"take_pip": "0",                  снятия из бонуса
//"take_balance": "530",           снятия из баланса
//"return_status": 1,               не нужно это учитоват не нужно для клиента
//"pay_status": 2,                 аперация
//"pay_name": "Сняти баланс из магазин",           тип операция
//"car_pip_size": "20002,30",     бонус клинета

//return payCode switch
//{
//    0 => "Сняти из бонус",
//    1 => "Зачислено",
//    2 => "Сняти из баланс",
//    3 => "Сняти бонус из магазин",
//    4 => "Сняти баланс из магазин",
//    _ => "Неизвестно"
//};