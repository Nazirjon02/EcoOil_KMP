package org.example.networking

import kotlinx.serialization.Serializable

@Serializable
data class PhoneResponse(
    val code: Int,
    val message: String,
    val data: PhoneData? = null
)

@Serializable
data class PhoneData(
    val token: String?=null,
    val car_id: Int?=null,
)
