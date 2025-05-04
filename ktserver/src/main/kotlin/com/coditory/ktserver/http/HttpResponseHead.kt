package com.coditory.ktserver.http

data class HttpResponseHead(
    val status: HttpResponseStatus,
    val headers: HttpParams = HttpParams.empty(),
) {
    fun toHttpString(): String {
        val builder = StringBuilder(status.toHttpString())
        headers.toHttpHeadersString()
        return builder.toString()
    }

    companion object {
        val OK = HttpResponseHead(HttpResponseStatus.OK)
    }
}
