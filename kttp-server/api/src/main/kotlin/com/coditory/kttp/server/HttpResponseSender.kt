package com.coditory.kttp.server

interface HttpResponseSender {
    suspend fun sendResponse(exchange: HttpExchange, response: HttpResponse)
}
