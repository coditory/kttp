package com.coditory.ktserver.http

import java.net.URI

data class HttpRequestHead(
    val method: HttpRequestMethod,
    val uri: URI,
    val headers: HttpParams = HttpParams.empty(),
) {
    fun toHttpString(): String {
        val builder = StringBuilder()
        builder.append(method.toString()).append(" ").append(uri.toString())
        headers.toHttpHeadersString()
        return builder.toString()
    }
}
