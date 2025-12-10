package org.example.project


import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*

actual fun PlatformHttpEngine(): HttpClientEngine = Darwin.create()
