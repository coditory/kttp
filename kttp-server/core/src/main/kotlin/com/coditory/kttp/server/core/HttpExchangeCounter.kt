package com.coditory.kttp.server.core

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.decrementAndFetch
import kotlin.concurrent.atomics.incrementAndFetch

@OptIn(ExperimentalAtomicApi::class)
internal class HttpExchangeCounter {
    private val unlockSignal = MutableSharedFlow<Unit>(
        replay = 1,
        // Important for tryEmit with replay=1
        // if you want to guarantee it doesn't suspend
        // and always updates the replay cache.
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private val ongoingExchanges = AtomicLong(0)

    fun increment() {
        ongoingExchanges.incrementAndFetch()
    }

    fun decrement() {
        ongoingExchanges.decrementAndFetch()
        unlockSignal.tryEmit(Unit)
    }

    fun value(): Long {
        return ongoingExchanges.load()
    }

    suspend fun waitForZero() {
        while (ongoingExchanges.load() > 0) {
            unlockSignal.first()
        }
    }
}
