package com.coditory.ktserver

import com.coditory.ktserver.http.HttpResponse

fun interface HttpAction {
    suspend fun handle(exchange: HttpExchange): HttpResponse
}
