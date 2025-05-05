package com.coditory.ktserver

import com.coditory.ktserver.http.HttpRequest

data class HttpExchange(
    val request: HttpRequest,
)
