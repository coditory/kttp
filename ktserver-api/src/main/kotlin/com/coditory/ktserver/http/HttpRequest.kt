package com.coditory.ktserver.http

import com.coditory.ktserver.HttpDeserializer
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.serialization.DeserializationStrategy
import java.net.URI

class HttpRequest(
    val method: HttpRequestMethod,
    val uri: URI,
    val headers: HttpParams = HttpParams.empty(),
    private val deserializer: HttpDeserializer,
    val source: Source,
) {
    suspend fun readBodyAsString() = source.readString()

    suspend fun <T> readBodyAs(strategy: DeserializationStrategy<T>): T {
        return deserializer.deserialize(strategy, toHttpRequestHead(), source)
    }

    fun toHttpRequestHead() = HttpRequestHead(
        method = method,
        uri = uri,
        headers = headers,
    )
}
