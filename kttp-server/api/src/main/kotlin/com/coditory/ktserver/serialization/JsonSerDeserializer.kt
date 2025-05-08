package com.coditory.ktserver.serialization

import com.coditory.ktserver.http.HttpRequestHead
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.io.decodeFromSource
import kotlinx.serialization.json.io.encodeToSink

class JsonSerDeserializer(
    private val json: Json,
) : ScoredHttpSerDeserializer {
    override fun serializationScore(request: HttpRequestHead): Int {
        val contentType = request.headers["Content-Type"]
        return contentType?.indexOf("/json") ?: -1
    }

    override suspend fun <T> serializeToString(
        value: T,
        strategy: SerializationStrategy<T>,
        request: HttpRequestHead,
    ): String {
        return json.encodeToString(strategy, value)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun <T> serialize(
        value: T,
        strategy: SerializationStrategy<T>,
        request: HttpRequestHead,
        sink: Sink,
    ) {
        json.encodeToSink(strategy, value, sink)
    }

    override fun deserializationScore(request: HttpRequestHead): Int {
        val contentType = request.headers["Content-Type"]
        return contentType?.indexOf("/json") ?: -1
    }

    override suspend fun <T> deserializeFromString(
        strategy: DeserializationStrategy<T>,
        request: HttpRequestHead,
        text: String,
    ): T {
        return json.decodeFromString(strategy, text)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun <T> deserialize(
        strategy: DeserializationStrategy<T>,
        request: HttpRequestHead,
        source: Source,
    ): T {
        return json.decodeFromSource(strategy, source)
    }

    companion object {
        private val DEFAULT by lazy { JsonSerDeserializer(Json) }

        fun default() = DEFAULT
    }
}
