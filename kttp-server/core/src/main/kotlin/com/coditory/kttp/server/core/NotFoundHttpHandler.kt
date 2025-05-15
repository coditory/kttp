package com.coditory.kttp.server.core

import com.coditory.kttp.HttpStatus
import com.coditory.kttp.server.HttpExchange
import com.coditory.kttp.server.HttpHandler
import com.coditory.kttp.server.HttpResponse

class NotFoundHttpHandler : HttpHandler {
    override suspend fun handle(exchange: HttpExchange): HttpResponse {
        return HttpResponse.StatusResponse(HttpStatus.NotFound)
    }
}
