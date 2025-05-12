package com.coditory.kttp.server

import com.coditory.kttp.HttpRequestHead
import com.coditory.kttp.HttpRequestMethod
import com.coditory.kttp.headers.HttpHeaders
import com.coditory.kttp.serialization.Deserializer
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.serialization.DeserializationStrategy
import java.net.URI

class HttpRequest(
    val method: HttpRequestMethod,
    val uri: URI,
    val headers: HttpHeaders = HttpHeaders.empty(),
    private val deserializer: Deserializer,
    val source: Source,
) {
    suspend fun readBodyAsString() = source.readString()

    suspend fun <T> readBodyAs(strategy: DeserializationStrategy<T>): T {
        return deserializer.deserialize(strategy, toHead(), source)
    }

    fun toHead() = HttpRequestHead(
        method = method,
        uri = uri,
        headers = headers,
    )
}
