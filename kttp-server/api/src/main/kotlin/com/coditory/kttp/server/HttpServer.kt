package com.coditory.kttp.server

interface HttpServer {
    fun start()
    fun stop()
    fun router(): HttpRouter
    fun routing(config: HttpRoute.() -> Unit) {
        router().routing(HttpRequestMatcher.matchingAll(), config)
    }
}
