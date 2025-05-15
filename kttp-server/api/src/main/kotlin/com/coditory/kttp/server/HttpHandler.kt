package com.coditory.kttp.server

fun interface HttpHandler {
    suspend fun handle(exchange: HttpExchange): HttpResponse
}
