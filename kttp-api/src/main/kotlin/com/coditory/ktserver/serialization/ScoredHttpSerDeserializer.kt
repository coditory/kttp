package com.coditory.ktserver.serialization

import com.coditory.ktserver.HttpSerDeserializer
import com.coditory.ktserver.http.HttpRequestHead
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy

interface ScoredHttpSerDeserializer :
    ScoredHttpSerializer, ScoredHttpDeserializer, HttpSerDeserializer {
    companion object {
        private val DEFAULT by lazy {
            CompositeHttpSerDeserializer(
                serializer = ScoredHttpSerializer.default(),
                deserializer = ScoredHttpDeserializer.default(),
            )
        }

        fun default(): ScoredHttpSerDeserializer = DEFAULT

        fun composite(
            serializer: ScoredHttpSerializer,
            deserializer: ScoredHttpDeserializer,
        ): ScoredHttpSerDeserializer = CompositeHttpSerDeserializer(serializer, deserializer)
    }
}

private class CompositeHttpSerDeserializer(
    private val serializer: ScoredHttpSerializer,
    private val deserializer: ScoredHttpDeserializer,
) : ScoredHttpSerDeserializer {
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
