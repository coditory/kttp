package com.coditory.kttp.server

import com.coditory.kttp.HttpRequestHead
import com.coditory.kttp.HttpRequestMethod
import com.coditory.kttp.headers.HttpHeaders
import com.coditory.kttp.serialization.Deserializer
import com.coditory.kttp.serialization.HttpSerDeserializer
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.serialization.DeserializationStrategy
import java.net.URI

class HttpRequest(
    val method: HttpRequestMethod,
    val uri: URI,
    val headers: HttpHeaders = HttpHeaders.empty(),
    private val deserializer: Deserializer = HttpSerDeserializer.default(),
    val source: Source = Buffer(),
) {
    constructor(
        method: HttpRequestMethod,
        uri: String,
        headers: HttpHeaders = HttpHeaders.empty(),
        deserializer: Deserializer = HttpSerDeserializer.default(),
        source: Source = Buffer(),
    ) : this(
        method = method,
        uri = URI(uri),
        headers = headers,
        deserializer = deserializer,
        source = source,
    )

    fun copy(
        method: HttpRequestMethod = this.method,
        uri: URI = this.uri,
        headers: HttpHeaders = this.headers,
        source: Source = this.source,
    ): HttpRequest {
        return HttpRequest(
            method = method,
            uri = uri,
            headers = headers,
            source = source,
            deserializer = this.deserializer,
        )
    }

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
