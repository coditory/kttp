package com.coditory.ktserver

import com.coditory.ktserver.http.HttpRequest

interface HttpExchange {
    val request: HttpRequest
    val channel: HttpChannel
}
