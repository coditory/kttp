package com.coditory.ktserver

import com.coditory.ktserver.http.HttpRequestHead
import com.coditory.ktserver.serialization.FormUrlEncodedFormat
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.io.decodeFromSource

interface HttpDeserializer {
    fun matches(request: HttpRequestHead): Int
    suspend fun <T> deserialize(strategy: DeserializationStrategy<T>, request: HttpRequestHead, source: Source): T

    companion object {
        fun default() = composite(listOf(json(), formUrlEncoded()))

        fun composite(
            deserializers: List<HttpDeserializer>,
            defaultDeserializer: HttpDeserializer? = deserializers.first(),
        ): HttpDeserializer = CompositeDeserializer(deserializers, defaultDeserializer)

        fun json(json: Json = Json): HttpDeserializer = JsonDeserializer(json)

        fun formUrlEncoded(format: FormUrlEncodedFormat = FormUrlEncodedFormat.default()): HttpDeserializer = FormUrlEncodedDeserializer(format)
    }
}

private class CompositeDeserializer(
    private val deserializers: List<HttpDeserializer>,
    private val defaultDeserializer: HttpDeserializer? = null,
) : HttpDeserializer {
    override fun matches(request: HttpRequestHead): Int {
        return deserializers
            .map { it.matches(request) }
            .filter { it >= 0 }
            .minOrNull() ?: -1
    }

    override suspend fun <T> deserialize(
        strategy: DeserializationStrategy<T>,
        request: HttpRequestHead,
        source: Source,
    ): T {
        val deserializer = deserializers
            .map { it to it.matches(request) }
            .filter { it.second >= 0 }
            .minByOrNull { it.second }
            ?.first
            ?: defaultDeserializer
            ?: throw IllegalStateException("No deserializer found for: ${request.method} ${request.uri}")
        return deserializer.deserialize(strategy, request, source)
    }
}

private class JsonDeserializer(private val json: Json) : HttpDeserializer {
    override fun matches(request: HttpRequestHead): Int {
        val contentType = request.headers["Content-Type"]
        return contentType?.indexOf("/json") ?: -1
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun <T> deserialize(
        strategy: DeserializationStrategy<T>,
        request: HttpRequestHead,
        source: Source,
    ): T {
        return json.decodeFromSource(strategy, source)
    }
}

private class FormUrlEncodedDeserializer(
    private val format: FormUrlEncodedFormat = FormUrlEncodedFormat.default(),
) : HttpDeserializer {
    override fun matches(request: HttpRequestHead): Int {
        val contentType = request.headers["Content-Type"]
        return contentType?.indexOf("application/x-www-form-urlencoded") ?: -1
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun <T> deserialize(
        strategy: DeserializationStrategy<T>,
        request: HttpRequestHead,
        source: Source,
    ): T {
        val text = source.readString()
        return format.decodeFromString(strategy, text)
    }
}
