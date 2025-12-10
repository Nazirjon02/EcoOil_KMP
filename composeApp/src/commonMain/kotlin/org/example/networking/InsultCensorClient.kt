package org.example.networking

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException
import org.example.util.NetworkError
import org.example.util.Result

class InsultCensorClient(
    val http: HttpClient,
    val baseUrl: String
) {

    suspend inline fun <reified T> request(
        path: String,
        method: HttpMethod = HttpMethod.Post,
        bodyObj: Any? = null,
        params: Map<String, String>? = null
    ): Result<T, NetworkError> {

        val response = try {
            http.request("$baseUrl$path") {

                this.method = method

                // Query params for GET
                params?.forEach { (k, v) ->
                    parameter(k, v)
                }

                // Body for POST
                if (bodyObj != null) {
                    contentType(ContentType.Application.Json)
                    setBody(bodyObj)
                }
            }
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkError.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(NetworkError.SERIALIZATION)
        }

        return when (response.status.value) {
            in 200..299 -> {
                val data = response.body<T>()
                Result.Success(data)
            }
            401 -> Result.Error(NetworkError.UNAUTHORIZED)
            409 -> Result.Error(NetworkError.CONFLICT)
            408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
            413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }
}
