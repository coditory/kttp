package com.coditory.ktserver

import java.nio.ByteBuffer

interface HttpChannel : HttpReadChannel, HttpWriteChannel

interface HttpReadChannel {
    suspend fun read(buffer: ByteBuffer): Int

    suspend fun readAsText(): String {
        val buffer = ByteBuffer.allocate(1024 * 8)
        val builder = StringBuilder()
        while (read(buffer) > 0) {
            builder.append(buffer)
        }
        return builder.toString()
    }
}

interface HttpWriteChannel {
    suspend fun write(buffer: ByteBuffer)
}
