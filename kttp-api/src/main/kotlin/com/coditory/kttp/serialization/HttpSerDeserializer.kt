package com.coditory.kttp.serialization

import com.coditory.kttp.HttpRequestHead
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

interface HttpSerDeserializer :
    HttpSerializer, HttpDeserializer, SerDeserializer {
    companion object {
        fun default(json: Json = Json): HttpSerDeserializer = CompositeHttpSerDeserializer(
            serializer = HttpSerializer.default(json),
            deserializer = HttpDeserializer.default(json),
        )

        fun composite(
            serializer: HttpSerializer,
            deserializer: HttpDeserializer,
        ): HttpSerDeserializer = CompositeHttpSerDeserializer(serializer, deserializer)
    }
}

private class CompositeHttpSerDeserializer(
    private val serializer: HttpSerializer,
    private val deserializer: HttpDeserializer,
) : HttpSerDeserializer {
    override fun serializationScore(request: HttpRequestHead): Int {
        return serializer.serializationScore(request)
    }

    override fun deserializationScore(request: HttpRequestHead): Int {
        return deserializer.deserializationScore(request)
    }

    override suspend fun <T> serializeToString(
        value: T,
        strategy: SerializationStrategy<T>,
        request: HttpRequestHead,
    ): String {
        return serializer.serializeToString(value, strategy, request)
    }

    override suspend fun <T> serialize(
        value: T,
        strategy: SerializationStrategy<T>,
        request: HttpRequestHead,
        sink: Sink,
    ) {
        serializer.serialize(value, strategy, request, sink)
    }

    override suspend fun <T> deserializeFromString(
        strategy: DeserializationStrategy<T>,
        request: HttpRequestHead,
        text: String,
    ): T {
        return deserializer.deserializeFromString(strategy, request, text)
    }

    override suspend fun <T> deserialize(
        strategy: DeserializationStrategy<T>,
        request: HttpRequestHead,
        source: Source,
    ): T {
        return deserializer.deserialize(strategy, request, source)
    }
}
