package com.coditory.kttp.serialization

import com.coditory.kttp.HttpRequestHead
import kotlinx.io.Sink
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

interface ScoredHttpSerializer : HttpSerializer {
    fun serializationScore(request: HttpRequestHead): Int

    companion object {
        fun default() = composite(listOf(JsonSerDeserializer.default(), FormUrlEncodedSerDeserializer.default()))

        fun composite(
            serializers: List<ScoredHttpSerializer>,
            defaultSerializer: ScoredHttpSerializer? = serializers.first(),
        ): ScoredHttpSerializer = CompositeSerializer(serializers, defaultSerializer)

        fun json(json: Json = Json): ScoredHttpSerializer = JsonSerDeserializer(json)

        fun formUrlEncoded(format: FormUrlEncodedFormat = FormUrlEncodedFormat.default()): ScoredHttpSerializer = FormUrlEncodedSerDeserializer(format)
    }
}

private class CompositeSerializer(
    private val serializers: List<ScoredHttpSerializer>,
    private val defaultSerializer: ScoredHttpSerializer? = serializers.first(),
) : ScoredHttpSerializer {
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

    private fun findSerializer(request: HttpRequestHead): HttpSerializer {
        return serializers
            .map { it to it.serializationScore(request) }
            .filter { it.second >= 0 }
            .minByOrNull { it.second }
            ?.first
            ?: defaultSerializer
            ?: throw IllegalStateException("No serializer found for: ${request.method} ${request.uri}")
    }
}
