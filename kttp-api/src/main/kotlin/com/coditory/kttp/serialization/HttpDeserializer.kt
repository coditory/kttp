package com.coditory.kttp.serialization

import com.coditory.kttp.HttpRequestHead
import kotlinx.io.Source
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json

interface HttpDeserializer : Deserializer {
    fun deserializationScore(request: HttpRequestHead): Int

    companion object {
        fun default() = composite(listOf(JsonSerDeserializer.default(), FormUrlEncodedSerDeserializer.default()))

        fun composite(
            deserializers: List<HttpDeserializer>,
            defaultDeserializer: HttpDeserializer? = deserializers.first(),
        ): HttpDeserializer = CompositeHttpDeserializer(deserializers, defaultDeserializer)

        fun json(json: Json = Json): HttpDeserializer = JsonSerDeserializer(json)

        fun formUrlEncoded(format: FormUrlEncodedFormat = FormUrlEncodedFormat.default()): HttpDeserializer = FormUrlEncodedSerDeserializer(format)
    }
}

private class CompositeHttpDeserializer(
    private val deserializers: List<HttpDeserializer>,
    private val defaultDeserializer: HttpDeserializer? = null,
) : HttpDeserializer {
    override fun deserializationScore(request: HttpRequestHead): Int {
        return deserializers
            .map { it.deserializationScore(request) }
            .filter { it >= 0 }
            .minOrNull() ?: -1
    }

    override suspend fun <T> deserializeFromString(
        strategy: DeserializationStrategy<T>,
        request: HttpRequestHead,
        text: String,
    ): T {
        val deserializer = findDeserializer(request)
        return deserializer.deserializeFromString(strategy, request, text)
    }

    override suspend fun <T> deserialize(
        strategy: DeserializationStrategy<T>,
        request: HttpRequestHead,
        source: Source,
    ): T {
        val deserializer = findDeserializer(request)
        return deserializer.deserialize(strategy, request, source)
    }

    private fun findDeserializer(request: HttpRequestHead): HttpDeserializer {
        return deserializers
            .map { it to it.deserializationScore(request) }
            .filter { it.second >= 0 }
            .minByOrNull { it.second }
            ?.first
            ?: defaultDeserializer
            ?: throw IllegalStateException("No deserializer found for: ${request.method} ${request.uri}")
    }
}
