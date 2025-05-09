package com.coditory.kttp

data class HttpExchangeHead(
    val request: HttpRequestHead,
    val response: HttpResponseHead,
)
