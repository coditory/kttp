package com.coditory.ktserver

interface HttpServer {
    fun start()
    fun stop()
    fun route(path: String)
}
