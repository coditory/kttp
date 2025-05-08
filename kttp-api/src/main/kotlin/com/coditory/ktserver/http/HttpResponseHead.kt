package com.coditory.ktserver.http

import kotlinx.io.Sink
import kotlinx.io.writeString

data class HttpResponseHead(
    val status: HttpStatus,
    val headers: HttpParams = HttpParams.empty(),
) {
    fun toHttpString(sink: Sink) {
        sink.writeInt(status.code)
        sink.writeString("\n")
    }

    fun toHttpString(): String {
        val builder = StringBuilder(status.toHttpString())
        headers.toHttpHeadersString()
        return builder.toString()
    }

    companion object {
        val OK = HttpResponseHead(HttpStatus.OK)
    }
}
