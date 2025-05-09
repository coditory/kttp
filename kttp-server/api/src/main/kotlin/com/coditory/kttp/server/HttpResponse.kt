package com.coditory.kttp.server

import com.coditory.kttp.HttpParams
import com.coditory.kttp.HttpResponseHead
import com.coditory.kttp.HttpStatus
import kotlinx.serialization.SerializationStrategy

sealed interface HttpResponse {
    val status: HttpStatus
    val headers: HttpParams

    fun toHttpResponseHead() = HttpResponseHead(
        status = status,
        headers = headers,
    )

    data class SentResponse(
        override val status: HttpStatus = HttpStatus.OK,
        override val headers: HttpParams = HttpParams.empty(),
    ) : HttpResponse

    data class StatusResponse(
        override val status: HttpStatus = HttpStatus.OK,
        override val headers: HttpParams = HttpParams.empty(),
    ) : HttpResponse

    data class TextResponse(
        val body: String,
        override val status: HttpStatus = HttpStatus.OK,
        override val headers: HttpParams = HttpParams.empty(),
    ) : HttpResponse

    data class SerializableResponse<T>(
        val body: T,
        val serializer: SerializationStrategy<T>,
        override val status: HttpStatus = HttpStatus.OK,
        override val headers: HttpParams = HttpParams.empty(),
    ) : HttpResponse
}
