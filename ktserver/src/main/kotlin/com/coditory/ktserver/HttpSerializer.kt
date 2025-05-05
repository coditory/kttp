package com.coditory.ktserver

import com.coditory.ktserver.http.HttpRequestHead
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy

interface HttpSerializer {
    suspend fun <T> serialize(value: T, strategy: SerializationStrategy<T>, request: HttpRequestHead, sink: Sink)
}

interface HttpDeserializer {
    suspend fun <T> deserialize(strategy: DeserializationStrategy<T>, request: HttpRequestHead, source: Source): T
}

interface HttpSerDeserializer : HttpSerializer, HttpDeserializer
