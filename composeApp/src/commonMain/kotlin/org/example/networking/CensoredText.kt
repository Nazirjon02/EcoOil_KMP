package org.example.networking

import kotlinx.serialization.Serializable

@Serializable
data class PhoneRequest(val phone: String,val deviceId:String,val hash:String)

@Serializable
data class PhoneResponse(val exists: Boolean)
