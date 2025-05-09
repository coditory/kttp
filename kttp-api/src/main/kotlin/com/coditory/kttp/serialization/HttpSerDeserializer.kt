package com.coditory.kttp.serialization

import com.coditory.kttp.HttpExchangeHead
import com.coditory.kttp.HttpRequestHead
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy

interface HttpSerializer {
    suspend fun <T> serializeToString(value: T, strategy: SerializationStrategy<T>, request: HttpRequestHead): String
    suspend fun <T> serialize(value: T, strategy: SerializationStrategy<T>, request: HttpRequestHead, sink: Sink)
}

interface HttpDeserializer {
    suspend fun <T> deserializeFromString(
        strategy: DeserializationStrategy<T>,
        request: HttpRequestHead,
        text: String,
    ): T

    suspend fun <T> deserializeFromString(
        strategy: DeserializationStrategy<T>,
        exchange: HttpExchangeHead,
        text: String,
    ): T = deserializeFromString(strategy, exchange.request, text)

    suspend fun <T> deserialize(strategy: DeserializationStrategy<T>, request: HttpRequestHead, source: Source): T
    suspend fun <T> deserialize(strategy: DeserializationStrategy<T>, exchange: HttpExchangeHead, source: Source): T = deserialize(strategy, exchange.request, source)
}

interface HttpSerDeserializer : HttpSerializer, HttpDeserializer
