package org.example.data

import kotlinx.serialization.Serializable

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
