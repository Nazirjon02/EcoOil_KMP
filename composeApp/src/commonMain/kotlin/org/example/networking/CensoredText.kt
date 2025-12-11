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
    val exists: Boolean? = null
)
