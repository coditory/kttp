package com.coditory.ktserver.nio

import com.coditory.ktserver.HttpChannel
import kotlinx.coroutines.suspendCancellableCoroutine
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousSocketChannel
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class HttpAsyncSocketChannel(
    private val channel: AsynchronousSocketChannel,
) : HttpChannel {
    override suspend fun read(buffer: ByteBuffer): Int {
        return suspendCancellableCoroutine { cont ->
            channel.read(
                buffer,
                null,
                object : java.nio.channels.CompletionHandler<Int, Void?> {
                    override fun completed(result: Int, attachment: Void?) {
                        cont.resume(result)
                    }

                    override fun failed(exc: Throwable, attachment: Void?) {
                        cont.resumeWithException(exc)
                    }
                },
            )
        }
    }

    override suspend fun write(buffer: ByteBuffer) {
        while (buffer.hasRemaining()) {
            suspendCancellableCoroutine { cont ->
                channel.write(
                    buffer,
                    null,
                    object : java.nio.channels.CompletionHandler<Int, Void?> {
                        override fun completed(result: Int, attachment: Void?) {
                            cont.resume(result)
                        }

                        override fun failed(exc: Throwable, attachment: Void?) {
                            cont.resumeWithException(exc)
                        }
                    },
                )
            }
        }
    }
}
