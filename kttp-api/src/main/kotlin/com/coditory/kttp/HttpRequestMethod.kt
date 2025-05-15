package com.coditory.kttp

import java.util.concurrent.ConcurrentHashMap

class HttpRequestMethod private constructor(
    private val value: String,
) : HttpSerializable {
    override fun toString() = value
    override fun toHttpString(builder: Appendable) {
        builder.append(value)
    }

    companion object {
        private val otherCache = SmallCache<String, HttpRequestMethod>()
        val GET = HttpRequestMethod("GET")
        val POST = HttpRequestMethod("POST")
        val PUT = HttpRequestMethod("PUT")
        val PATCH = HttpRequestMethod("PATCH")
        val DELETE = HttpRequestMethod("DELETE")
        val HEAD = HttpRequestMethod("HEAD")
        val OPTIONS = HttpRequestMethod("OPTIONS")

        fun from(text: String): HttpRequestMethod {
            val uppercased = text.uppercase()
            return when (uppercased) {
                GET.value -> GET
                POST.value -> POST
                PUT.value -> PUT
                PATCH.value -> PATCH
                DELETE.value -> DELETE
                HEAD.value -> HEAD
                OPTIONS.value -> OPTIONS
                else -> otherCache.get(uppercased) { HttpRequestMethod(uppercased) }
            }
        }
    }
}

private class SmallCache<K, V>(
    private val maxSize: Int = 100,
) {
    private val cache = ConcurrentHashMap<K, V>()
    private val ordered = mutableListOf<K>()

    fun get(key: K): V? {
        return cache[key]
    }

    fun get(key: K, provider: () -> V): V {
        val value = cache[key]
        if (value != null) {
            return value
        }
        val created = provider()
        put(key, created)
        return created
    }

    fun put(key: K, value: V) {
        val prev = cache.put(key, value)
        if (prev == null) {
            ordered.add(key)
            if (cache.size > maxSize) {
                val first = ordered.removeFirst()
                cache.remove(first)
            }
        }
    }
}
