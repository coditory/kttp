package com.coditory.kttp

import java.net.URI

data class HttpRequestHead(
    val method: HttpRequestMethod,
    val uri: URI,
    val version: HttpVersion = HttpVersion.HTTP_2_0,
    val headers: HttpHeaders = HttpHeaders.empty(),
) {
    fun toHttpString(builder: Appendable) {
        method.toHttpString(builder)
        builder.append(" ")
        builder.append(uri.toString())
        builder.append(" ")
        version.toHttpString(builder)
        builder.append("\r\n")
        headers.toHttpString(builder)
    }
}
