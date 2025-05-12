package com.coditory.kttp.server

import com.coditory.kttp.HttpResponseHead
import com.coditory.kttp.HttpStatus
import com.coditory.kttp.headers.HttpHeaders
import kotlinx.serialization.SerializationStrategy

sealed interface HttpResponse {
    val status: HttpStatus
    val headers: HttpHeaders

    fun toHead() = HttpResponseHead(
        status = status,
        headers = headers,
    )

    data class SentResponse(
        override val status: HttpStatus = HttpStatus.OK,
        override val headers: HttpHeaders = HttpHeaders.empty(),
    ) : HttpResponse

    data class StatusResponse(
        override val status: HttpStatus = HttpStatus.OK,
        override val headers: HttpHeaders = HttpHeaders.empty(),
    ) : HttpResponse

    data class TextResponse(
        val body: String,
        override val status: HttpStatus = HttpStatus.OK,
        override val headers: HttpHeaders = HttpHeaders.empty(),
    ) : HttpResponse

    data class SerializableResponse<T>(
        val body: T,
        val serializer: SerializationStrategy<T>,
        override val status: HttpStatus = HttpStatus.OK,
        override val headers: HttpHeaders = HttpHeaders.empty(),
    ) : HttpResponse
}
