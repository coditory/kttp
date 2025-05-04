package com.coditory.ktserver

import com.coditory.ktserver.http.HttpRequestHead
import com.coditory.ktserver.serialization.FormUrlEncodedFormat
import kotlinx.io.Sink
import kotlinx.io.writeString
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.io.encodeToSink

interface HttpSerializer {
    fun matches(request: HttpRequestHead): Int
    suspend fun <T> serialize(value: T, strategy: SerializationStrategy<T>, request: HttpRequestHead, sink: Sink)

    companion object {
        fun default() = composite(listOf(json(), formUrlEncoded()))

        fun composite(
            serializers: List<HttpSerializer>,
            defaultSerializer: HttpSerializer? = serializers.first(),
        ): HttpSerializer = CompositeSerializer(serializers, defaultSerializer)

        fun json(json: Json = Json): HttpSerializer = JsonSerializer(json)

        fun formUrlEncoded(format: FormUrlEncodedFormat = FormUrlEncodedFormat.default()): HttpSerializer = FormUrlEncodedSerializer(format)
    }
}

private class CompositeSerializer(
    private val serializers: List<HttpSerializer>,
    private val defaultSerializer: HttpSerializer? = serializers.first(),
) : HttpSerializer {
    override fun matches(request: HttpRequestHead): Int {
        return serializers
            .map { it.matches(request) }
            .filter { it >= 0 }
            .minOrNull() ?: -1
    }

    override suspend fun <T> serialize(
        value: T,
        strategy: SerializationStrategy<T>,
        request: HttpRequestHead,
        sink: Sink,
    ) {
        val serializer = serializers
            .map { it to it.matches(request) }
            .filter { it.second >= 0 }
            .minByOrNull { it.second }
            ?.first
            ?: defaultSerializer
            ?: throw IllegalStateException("No serializer found for: ${request.method} ${request.uri}")
        return serializer.serialize(value, strategy, request, sink)
    }
}

private class JsonSerializer(private val json: Json) : HttpSerializer {
    override fun matches(request: HttpRequestHead): Int {
        val contentType = request.headers["Content-Type"]
        return contentType?.indexOf("/json") ?: -1
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
}

private class FormUrlEncodedSerializer(
    private val format: FormUrlEncodedFormat = FormUrlEncodedFormat.default(),
) : HttpSerializer {
    override fun matches(request: HttpRequestHead): Int {
        val contentType = request.headers["Content-Type"]
        return contentType?.indexOf("application/x-www-form-urlencoded") ?: -1
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun <T> serialize(
        value: T,
        strategy: SerializationStrategy<T>,
        request: HttpRequestHead,
        sink: Sink,
    ) {
        val text = format.encodeToString(strategy, value)
        sink.writeString(text)
    }
}
