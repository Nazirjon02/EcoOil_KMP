package org.example.project



expect object Until {
    fun sha256(input: String): String
    fun getDeviceId(): String
    fun currentTimeMillis(): Long
}



