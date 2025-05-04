package com.coditory.ktserver.http

import com.coditory.quark.uri.UriComponents
import java.net.URI

interface HttpParams {
    fun asMap(): Map<String, List<String>>

    operator fun get(name: String): String? = getAll(name)?.firstOrNull()

    fun getAll(name: String): List<String>? = listForKey(name)

    operator fun contains(name: String): Boolean = listForKey(name) != null

    fun contains(name: String, value: String): Boolean = listForKey(name)?.contains(value) == true

    fun names(): Set<String> = asMap().keys

    fun isEmpty(): Boolean = asMap().isEmpty()

    fun entries(): Set<Map.Entry<String, List<String>>> = asMap().entries

    operator fun plus(other: HttpParams): HttpParams

    fun plus(vararg pairs: Pair<String, String>): HttpParams

    fun forEach(body: (String, List<String>) -> Unit) {
        for ((key, value) in asMap()) body(key, value)
    }

    fun forEachEntry(body: (String, String) -> Unit) {
        for ((key, value) in asMap()) {
            value.forEach { body(key, it) }
        }
    }

    fun toHttpHeadersString(): String {
        val builder = StringBuilder()
        toHttpHeadersString(builder)
        return builder.toString()
    }

    fun toHttpHeadersString(builder: StringBuilder) {
        this.forEachEntry { key, value ->
            builder.append(key).append(": ").append(value).append('\n')
        }
    }

    private fun listForKey(name: String): List<String>? = asMap()[name.lowercase()]

    companion object {
        fun empty(): HttpParams = MutableHttpParams.empty()

        fun from(vararg values: Pair<String, String>): HttpParams = MutableHttpParams.from(*values)

        fun from(values: Map<String, String>): HttpParams = MutableHttpParams.from(values)

        fun fromQueryParams(uri: URI): HttpParams = MutableHttpParams.fromQueryParams(uri)
    }
}

class MutableHttpParams private constructor(
    private val values: Map<String, List<String>> = mutableMapOf(),
) : HttpParams {
    override fun asMap() = values

    override fun plus(other: HttpParams): MutableHttpParams {
        val merged = mutableMapOf<String, List<String>>()
        merged.putAll(values)
        other.forEach { key, values ->
            val key = key.lowercase()
            val m = merged[key]
            if (m == null) {
                merged[key] = values
            } else {
                merged[key] = m.plus(values)
            }
        }
        return MutableHttpParams(merged)
    }

    override fun plus(vararg pairs: Pair<String, String>): MutableHttpParams {
        val merged = mutableMapOf<String, List<String>>()
        merged.putAll(values)
        pairs.forEach { pair ->
            val key = pair.first.lowercase()
            val value = pair.second
            val m = merged[key]
            if (m == null) {
                merged[key] = listOf(value)
            } else {
                merged[key] = m.plus(value)
            }
        }
        return MutableHttpParams(merged)
    }

    override fun toString(): String = values.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MutableHttpParams) return false
        return values == other.values
    }

    override fun hashCode(): Int {
        return values.hashCode()
    }

    companion object {
        private val empty = MutableHttpParams()

        fun empty() = empty

        fun from(vararg values: Pair<String, String>) = MutableHttpParams(
            values.groupBy({ it.first.lowercase() }, { it.second }),
        )

        fun from(values: Map<String, String>) = MutableHttpParams(
            values.map { it.key.lowercase() to listOf(it.value) }.toMap(),
        )

        fun fromQueryParams(uri: URI) = MutableHttpParams(
            UriComponents.from(uri).queryMultiParams,
        )
    }
}
