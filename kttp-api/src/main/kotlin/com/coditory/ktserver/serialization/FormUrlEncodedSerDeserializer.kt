package com.coditory.ktserver.serialization

import com.coditory.ktserver.http.HttpRequestHead
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy

class FormUrlEncodedSerDeserializer(
    private val format: FormUrlEncodedFormat,
) : ScoredHttpSerDeserializer {
    override fun serializationScore(request: HttpRequestHead): Int {
        val contentType = request.headers["Content-Type"]
        return contentType?.indexOf("application/x-www-form-urlencoded") ?: -1
    }

    override suspend fun <T> serializeToString(
        value: T,
        strategy: SerializationStrategy<T>,
        request: HttpRequestHead,
    ): String {
        return format.encodeToString(strategy, value)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun <T> serialize(
        value: T,
        strategy: SerializationStrategy<T>,
        request: HttpRequestHead,
        sink: Sink,
    ) {
        val text = serializeToString(value, strategy, request)
        sink.writeString(text)
    }

    override fun deserializationScore(request: HttpRequestHead): Int {
        val contentType = request.headers["Content-Type"]
        return contentType?.indexOf("application/x-www-form-urlencoded") ?: -1
    }

    override suspend fun <T> deserializeFromString(
        strategy: DeserializationStrategy<T>,
        request: HttpRequestHead,
        text: String,
    ): T {
        return format.decodeFromString(strategy, text)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun <T> deserialize(
        strategy: DeserializationStrategy<T>,
        request: HttpRequestHead,
        source: Source,
    ): T {
        val text = source.readString()
        return deserializeFromString(strategy, request, text)
    }

    companion object {
        private val DEFAULT by lazy { FormUrlEncodedSerDeserializer(FormUrlEncodedFormat.default()) }

        fun default() = DEFAULT
    }
}
