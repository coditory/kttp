package com.coditory.kttp

import com.coditory.kttp.headers.HttpHeaders

data class HttpResponseHead(
    val status: HttpStatus,
    val headers: HttpHeaders = HttpHeaders.empty(),
) : HttpSerializable {
    override fun toHttpString(builder: Appendable) {
        builder.append(status.toHttpString())
        headers.toHttpString(builder)
    }

    companion object {
        val OK = HttpResponseHead(HttpStatus.OK)
    }
}
