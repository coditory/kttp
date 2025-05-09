package com.coditory.kttp.server

interface HttpServer {
    fun start()
    fun stop()
    fun routing(config: HttpRoute.() -> Unit)
}
