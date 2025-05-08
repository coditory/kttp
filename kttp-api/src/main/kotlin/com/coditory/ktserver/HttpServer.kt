package com.coditory.ktserver

interface HttpServer {
    fun start()
    fun stop()
    fun routing(config: HttpRoute.() -> Unit)
}
