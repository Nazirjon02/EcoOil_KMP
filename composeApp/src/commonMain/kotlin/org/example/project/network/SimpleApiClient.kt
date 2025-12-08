//package org.example.project.network
//
//import io.ktor.client.*
//import io.ktor.client.call.*
//import io.ktor.client.plugins.*
//import io.ktor.client.plugins.logging.*
//import io.ktor.client.request.*
//import io.ktor.client.request.forms.submitForm
//import io.ktor.client.statement.*
//import io.ktor.http.*
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//
///**
// * Простой HTTP клиент с логированием
// */
//class SimpleApiClient(
//    private val baseUrl: String = "",
//    private val timeout: Long = 30000,
//    private val enableLogging: Boolean = true
//) {
//    private val client = createHttpClient()
//
//    private fun createHttpClient(): HttpClient {
//        return HttpClient {
//            install(HttpTimeout) {
//                requestTimeoutMillis = timeout
//                connectTimeoutMillis = timeout
//                socketTimeoutMillis = timeout
//            }
//
//            if (enableLogging) {
//                install(Logging) {
//                    logger = Logger.DEFAULT
//                    level = LogLevel.ALL
//
//                    logger = object : Logger {
//                        override fun log(message: String) {
//                            when {
//                                message.contains("REQUEST: ") -> {
//                                    println("\n╔════════════════════════════════════════╗")
//                                    println("║           🌐 HTTP ЗАПРОС              ║")
//                                    println("╚════════════════════════════════════════╝")
//                                    formatAndPrintRequest(message)
//                                }
//                                message.contains("RESPONSE: ") -> {
//                                    println("\n╔════════════════════════════════════════╗")
//                                    println("║           📥 HTTP ОТВЕТ               ║")
//                                    println("╚════════════════════════════════════════╝")
//                                    formatAndPrintResponse(message)
//                                }
//                                message.contains("BODY") -> {
//                                    formatAndPrintBody(message)
//                                }
//                                else -> {
//                                    if (message.isNotBlank() && !message.contains("->")) {
//                                        println("   📄 $message")
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            defaultRequest {
//                header(HttpHeaders.ContentType, ContentType.Application.Json)
//                header(HttpHeaders.Accept, ContentType.Application.Json)
//            }
//        }
//    }
//
//
//    private fun formatAndPrintRequest(log: String) {
//        val lines = log.lines()
//
//        lines.forEach { line ->
//            when {
//                line.contains("METHOD:") -> {
//                    val method = line.substringAfter("METHOD:").trim()
//                    val emoji = when (method.uppercase()) {
//                        "GET" -> "📤"
//                        "POST" -> "📝"
//                        "PUT" -> "🔄"
//                        "DELETE" -> "🗑️"
//                        else -> "📨"
//                    }
//                    println("   $emoji Метод: $method")
//                }
//                line.contains("URL:") -> {
//                    val url = line.substringAfter("URL:").trim()
//                    println("   🔗 URL: $url")
//                }
//                line.contains("HEADERS") -> {
//                    println("   📋 Заголовки:")
//                }
//                line.contains("CONTENT") -> {
//                    println("   📦 Тело запроса:")
//                }
//                line.trim().startsWith("{") || line.trim().startsWith("[") -> {
//                    println("   📝 ${tryFormatJson(line)}")
//                }
//                line.contains("=") && line.trim().isNotBlank() && !line.contains("->") -> {
//                    val parts = line.split("=", limit = 2)
//                    if (parts.size == 2) {
//                        val key = parts[0].trim()
//                        val value = parts[1].trim()
//                        if (key.equals("authorization", ignoreCase = true)) {
//                            println("      $key: ***скрыто***")
//                        } else {
//                            println("      $key: $value")
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private fun formatAndPrintResponse(log: String) {
//        val lines = log.lines()
//
//        lines.forEach { line ->
//            when {
//                line.contains("STATUS:") -> {
//                    val status = line.substringAfter("STATUS:").trim()
//                    val statusCode = status.substringBefore(" ").toIntOrNull() ?: 0
//                    val emoji = when (statusCode) {
//                        in 200..299 -> "✅"
//                        in 300..399 -> "🔄"
//                        in 400..499 -> "⚠️"
//                        in 500..599 -> "❌"
//                        else -> "📄"
//                    }
//                    println("   $emoji Статус: $status")
//                }
//                line.contains("RESPONSE TIME:") -> {
//                    val time = line.substringAfter("RESPONSE TIME:").trim()
//                    println("   ⏱️ Время ответа: $time")
//                }
//                line.contains("HEADERS") -> {
//                    println("   📋 Заголовки ответа:")
//                }
//                line.contains("BODY") -> {
//                    println("   📦 Тело ответа:")
//                }
//                line.trim().startsWith("{") || line.trim().startsWith("[") -> {
//                    println("   📝 ${tryFormatJson(line)}")
//                }
//                line.contains("=") && line.trim().isNotBlank() && !line.contains("->") -> {
//                    val parts = line.split("=", limit = 2)
//                    if (parts.size == 2) {
//                        println("      ${parts[0].trim()}: ${parts[1].trim()}")
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * Форматирование тела
//     */
//    private fun formatAndPrintBody(log: String) {
//        val jsonStart = log.indexOfFirst { it == '{' || it == '[' }
//        if (jsonStart != -1) {
//            val jsonString = log.substring(jsonStart)
//            println("   ${tryFormatJson(jsonString)}")
//        }
//    }
//
//    /**
//     * Попытка красиво форматировать JSON
//     */
//    private fun tryFormatJson(jsonString: String): String {
//        return try {
//            val trimmed = jsonString.trim()
//            // Простое форматирование JSON с отступами
//            var indentLevel = 0
//            val result = StringBuilder()
//
//            for (char in trimmed) {
//                when (char) {
//                    '{', '[' -> {
//                        result.append("\n${"   ".repeat(indentLevel)}$char")
//                        indentLevel++
//                        result.append("\n${"   ".repeat(indentLevel)}")
//                    }
//                    '}', ']' -> {
//                        indentLevel--
//                        result.append("\n${"   ".repeat(indentLevel)}$char")
//                    }
//                    ',' -> {
//                        result.append("$char\n${"   ".repeat(indentLevel)}")
//                    }
//                    ':' -> {
//                        result.append("$char ")
//                    }
//                    else -> {
//                        result.append(char)
//                    }
//                }
//            }
//
//            result.toString()
//        } catch (e: Exception) {
//            jsonString
//        }
//    }
//
//    /**
//     * GET запрос
//     */
//    suspend fun get(
//        endpoint: String,
//        queryParams: Map<String, Any> = emptyMap(),
//        headers: Map<String, String> = emptyMap()
//    ): SimpleApiResponse {
//        return withContext(Dispatchers.Default) {
//            try {
//                val url = if (baseUrl.isNotEmpty()) "$baseUrl/$endpoint" else endpoint
//
//                val response = client.get(url) {
//                    queryParams.forEach { (key, value) ->
//                        parameter(key, value.toString())
//                    }
//                    headers.forEach { (key, value) ->
//                        header(key, value)
//                    }
//                }
//
//                SimpleApiResponse.Success(
//                    data = response.bodyAsText(),
//                    statusCode = response.status.value,
//                    headers = response.headers
//                )
//            } catch (e: Exception) {
//                println("💥 Ошибка GET запроса: ${e.message}")
//                SimpleApiResponse.Error(
//                    message = e.message ?: "Неизвестная ошибка",
//                    statusCode = null,
//                    cause = e
//                )
//            }
//        }
//    }
//
//    /**
//     * POST запрос с raw данными
//     */
//    suspend fun post(
//        endpoint: String,
//        body: String? = null,
//        headers: Map<String, String> = emptyMap()
//    ): SimpleApiResponse {
//        return withContext(Dispatchers.Default) {
//            try {
//                val url = if (baseUrl.isNotEmpty()) "$baseUrl/$endpoint" else endpoint
//
//                val response = client.post(url) {
//                    if (body != null) {
//                        setBody(body)
//                    }
//                    headers.forEach { (key, value) ->
//                        header(key, value)
//                    }
//                }
//
//                SimpleApiResponse.Success(
//                    data = response.bodyAsText(),
//                    statusCode = response.status.value,
//                    headers = response.headers
//                )
//            } catch (e: Exception) {
//                println("💥 Ошибка POST запроса: ${e.message}")
//                SimpleApiResponse.Error(
//                    message = e.message ?: "Неизвестная ошибка",
//                    statusCode = null,
//                    cause = e
//                )
//            }
//        }
//    }
//
//    /**
//     * POST запрос с form данными
//     */
//    suspend fun postForm(
//        endpoint: String,
//        formData: Map<String, String> = emptyMap(),
//        headers: Map<String, String> = emptyMap()
//    ): SimpleApiResponse {
//        return withContext(Dispatchers.Default) {
//            try {
//                val url = if (baseUrl.isNotEmpty()) "$baseUrl/$endpoint" else endpoint
//
//                val response = client.submitForm(
//                    url = url,
//                    formParameters = Parameters.build {
//                        formData.forEach { (key, value) ->
//                            append(key, value)
//                        }
//                    }
//                ) {
//                    headers.forEach { (key, value) ->
//                        header(key, value)
//                    }
//                }
//
//                SimpleApiResponse.Success(
//                    data = response.bodyAsText(),
//                    statusCode = response.status.value,
//                    headers = response.headers
//                )
//            } catch (e: Exception) {
//                println("💥 Ошибка POST формы: ${e.message}")
//                SimpleApiResponse.Error(
//                    message = e.message ?: "Неизвестная ошибка",
//                    statusCode = null,
//                    cause = e
//                )
//            }
//        }
//    }
//
//    /**
//     * PUT запрос
//     */
//    suspend fun put(
//        endpoint: String,
//        body: String? = null,
//        headers: Map<String, String> = emptyMap()
//    ): SimpleApiResponse {
//        return withContext(Dispatchers.Default) {
//            try {
//                val url = if (baseUrl.isNotEmpty()) "$baseUrl/$endpoint" else endpoint
//
//                val response = client.put(url) {
//                    if (body != null) {
//                        setBody(body)
//                    }
//                    headers.forEach { (key, value) ->
//                        header(key, value)
//                    }
//                }
//
//                SimpleApiResponse.Success(
//                    data = response.bodyAsText(),
//                    statusCode = response.status.value,
//                    headers = response.headers
//                )
//            } catch (e: Exception) {
//                println("💥 Ошибка PUT запроса: ${e.message}")
//                SimpleApiResponse.Error(
//                    message = e.message ?: "Неизвестная ошибка",
//                    statusCode = null,
//                    cause = e
//                )
//            }
//        }
//    }
//
//    /**
//     * DELETE запрос
//     */
//    suspend fun delete(
//        endpoint: String,
//        headers: Map<String, String> = emptyMap()
//    ): SimpleApiResponse {
//        return withContext(Dispatchers.Default) {
//            try {
//                val url = if (baseUrl.isNotEmpty()) "$baseUrl/$endpoint" else endpoint
//
//                val response = client.delete(url) {
//                    headers.forEach { (key, value) ->
//                        header(key, value)
//                    }
//                }
//
//                SimpleApiResponse.Success(
//                    data = response.bodyAsText(),
//                    statusCode = response.status.value,
//                    headers = response.headers
//                )
//            } catch (e: Exception) {
//                println("💥 Ошибка DELETE запроса: ${e.message}")
//                SimpleApiResponse.Error(
//                    message = e.message ?: "Неизвестная ошибка",
//                    statusCode = null,
//                    cause = e
//                )
//            }
//        }
//    }
//
//    /**
//     * Простой тестовый запрос
//     */
//    suspend fun testConnection(url: String = "https://httpbin.org/get"): Boolean {
//        return try {
//            val response = get(url)
//            response is SimpleApiResponse.Success && response.statusCode in 200..299
//        } catch (e: Exception) {
//            false
//        }
//    }
//
//    /**
//     * Закрытие клиента
//     */
//    fun close() {
//        client.close()
//        println("🔌 HTTP клиент закрыт")
//    }
//}
//
//
//sealed class SimpleApiResponse {
//    data class Success(
//        val data: String,
//        val statusCode: Int,
//        val headers: Headers
//    ) : SimpleApiResponse()
//
//    data class Error(
//        val message: String,
//        val statusCode: Int?,
//        val cause: Throwable?
//    ) : SimpleApiResponse()
//}