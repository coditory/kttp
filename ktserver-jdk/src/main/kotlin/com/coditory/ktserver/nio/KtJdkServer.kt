package com.coditory.ktserver.nio

import com.coditory.ktserver.HttpServer
import com.coditory.quark.uri.Ports
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.io.Buffer
import kotlinx.io.asSource
import kotlinx.io.buffered
import kotlinx.io.readString
import java.net.InetSocketAddress
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import com.sun.net.httpserver.HttpServer as JdkHttpServer

class KtJdkServer(
    val port: Int = Ports.getNextAvailable(),
    val backlog: Int = 0,
    val requestScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
) : HttpServer {
    private val server = JdkHttpServer.create(InetSocketAddress(port), 0).apply {
        executor = null // creates a default executor
        createContext("/") { exchange ->
            requestScope.launch {
                val src = exchange.requestBody.asSource().buffered()
                src.readString()
                handleRequest(exchange)
            }
        }
    }

    override fun start() {
        val server = AsynchronousServerSocketChannel.open().bind(InetSocketAddress(port), backlog)
        runBlocking {
            val channel = accept(server)
            val asyncChannel = HttpAsyncSocketChannel(channel)
            Buffer
        }
    }

    suspend fun accept(server: AsynchronousServerSocketChannel): AsynchronousSocketChannel {
        suspendCancellableCoroutine { cont ->
            server.accept(
                null,
                object : java.nio.channels.CompletionHandler<AsynchronousSocketChannel, Void?> {
                    override fun completed(result: AsynchronousSocketChannel, attachment: Void?) {
                        cont.resume(result)
                    }

                    override fun failed(exc: Throwable, attachment: Void?) {
                        cont.resumeWithException(exc)
                    }
                },
            )
        }
    }

    private suspend fun handleRequest(channel: HttpAsyncSocketChannel) {
        try {
            // Read the request from the client
            val requestLine = source.readUtf8Line()
            println("Received request: $requestLine")

            // Prepare an HTTP-like response
            val body = "Hello from non-blocking Okio!"
            val response = """
            HTTP/1.1 200 OK
            Content-Type: text/plain
            Content-Length: ${body.length}

            $body
            """.trimIndent()

            // Write the response to the client
            sink.writeUtf8(response)
            sink.flush()
        } catch (e: Exception) {
            println("Error while handling client: ${e.message}")
        } finally {
            channel.close()
        }
    }

    override fun stop() {
        TODO("Not yet implemented")
    }

    override fun route(path: String) {
        TODO("Not yet implemented")
    }
}
