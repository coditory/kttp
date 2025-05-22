package com.coditory.kttp.serialization

import com.coditory.kttp.HttpRequestHead
import kotlinx.io.Sink
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

interface HttpSerializer : Serializer {
    fun serializationScore(request: HttpRequestHead): Int

    companion object {
        fun default(
            json: Json = Json,
            formUrlEncodedFormat: FormUrlEncodedFormat = FormUrlEncodedFormat(json.serializersModule),
        ) = composite(
            JsonSerDeserializer(json),
            FormUrlEncodedSerDeserializer(formUrlEncodedFormat),
        )

        fun composite(vararg serializers: HttpSerializer): HttpSerializer = CompositeHttpSerializer(serializers.toList())

        fun composite(
            serializers: List<HttpSerializer>,
            defaultSerializer: HttpSerializer? = serializers.firstOrNull(),
        ): HttpSerializer = CompositeHttpSerializer(serializers, defaultSerializer)

        fun json(json: Json = Json): HttpSerializer = JsonSerDeserializer(json)

        fun formUrlEncoded(format: FormUrlEncodedFormat = FormUrlEncodedFormat()): HttpSerializer = FormUrlEncodedSerDeserializer(format)
    }
}

private class CompositeHttpSerializer(
    private val serializers: List<HttpSerializer>,
    private val defaultSerializer: HttpSerializer? = serializers.firstOrNull(),
) : HttpSerializer {
    override fun serializationScore(request: HttpRequestHead): Int {
        return serializers
            .map { it.serializationScore(request) }
            .filter { it >= 0 }
            .minOrNull() ?: -1
    }

    override suspend fun <T> serializeToString(
        value: T,
        strategy: SerializationStrategy<T>,
        request: HttpRequestHead,
    ): String {
        val serializer = findSerializer(request)
        return serializer.serializeToString(value, strategy, request)
    }

    override suspend fun <T> serialize(
        value: T,
        strategy: SerializationStrategy<T>,
        request: HttpRequestHead,
        sink: Sink,
    ) {
        val serializer = findSerializer(request)
        return serializer.serialize(value, strategy, request, sink)
    }

    private fun findSerializer(request: HttpRequestHead): Serializer {
        return serializers
            .map { it to it.serializationScore(request) }
            .filter { it.second >= 0 }
            .minByOrNull { it.second }
            ?.first
            ?: defaultSerializer
            ?: throw IllegalStateException("No serializer found for: ${request.method} ${request.uri}")
    }
}
