package com.coditory.kttp.server

import com.coditory.kttp.HttpStatus

class NotFoundHttpHandler : HttpHandler {
    override suspend fun handle(exchange: HttpExchange): HttpResponse {
        return HttpResponse.StatusResponse(HttpStatus.Companion.NotFound)
    }
}
