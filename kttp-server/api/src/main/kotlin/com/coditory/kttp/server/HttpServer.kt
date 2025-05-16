package com.coditory.kttp.server

import kotlinx.coroutines.channels.Channel
import sun.misc.Signal

interface HttpServer {
    fun start()
    fun stop()
    fun router(): HttpRouter

    suspend fun startAndWait(onStart: () -> Unit = {}) {
        start()
        onStart()
        val channel = Channel<Unit>(0)
        Signal.handle(Signal("INT")) {
            channel.trySend(Unit)
        }
        channel.receive()
    }

    fun routing(config: HttpRoute.() -> Unit) {
        router().routing(HttpRequestMatcher.matchingAll(), config)
    }
}
