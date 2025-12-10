package org.example.project


import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*

actual fun PlatformHttpEngine(): HttpClientEngine = OkHttp.create()
