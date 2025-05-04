package com.coditory.ktserver.http

import kotlinx.serialization.SerializationStrategy

sealed interface HttpResponse {
    val status: HttpResponseStatus
    val headers: HttpParams

    fun toHttpResponseHead() = HttpResponseHead(
        status = status,
        headers = headers,
    )

    data class StatusResponse(
        override val status: HttpResponseStatus,
        override val headers: HttpParams,
    ) : HttpResponse

    data class TextResponse(
        override val status: HttpResponseStatus,
        override val headers: HttpParams,
        val body: String,
    ) : HttpResponse

    data class SerializableResponse<T>(
        override val status: HttpResponseStatus,
        override val headers: HttpParams,
        val body: T,
        val serializer: SerializationStrategy<T>,
    ) : HttpResponse
}
