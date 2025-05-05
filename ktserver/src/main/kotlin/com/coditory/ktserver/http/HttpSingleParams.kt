package com.coditory.ktserver.http

import com.coditory.quark.uri.UriComponents
import java.net.URI
import kotlin.collections.set

interface HttpSingleParams {
    fun asMap(): Map<String, String>

    operator fun get(name: String): String?

    operator fun contains(name: String): Boolean

    fun contains(name: String, value: String): Boolean

    fun names(): Set<String> = asMap().keys

    fun isEmpty(): Boolean = asMap().isEmpty()

    fun entries(): Set<Map.Entry<String, String>> = asMap().entries

    operator fun plus(other: HttpSingleParams): HttpSingleParams = with(other)

    operator fun minus(other: HttpSingleParams): HttpSingleParams = without(other)

    fun with(other: HttpSingleParams, override: Boolean = true): HttpSingleParams

    fun with(vararg pairs: Pair<String, String>, override: Boolean = true): HttpSingleParams

    fun with(name: String, value: String, override: Boolean = true): HttpSingleParams = with(name to value)

    fun without(other: HttpSingleParams): HttpSingleParams

    fun without(vararg pairs: Pair<String, String>): HttpSingleParams

    fun without(name: String): HttpSingleParams

    fun without(name: String, value: String): HttpSingleParams = without(name to value)

    fun forEach(body: (String, String) -> Unit) {
        for ((key, value) in asMap()) body(key, value)
    }

    companion object {
        fun empty(
            ignoreNameCase: Boolean = true,
            lowercaseNames: Boolean = true,
            ignoreValueCase: Boolean = false,
            lowercaseValues: Boolean = false,
            deduplicateEntries: Boolean = true,
        ): HttpParams = MutableHttpParams.empty()

        fun from(vararg values: Pair<String, String>): HttpParams = MutableHttpParams.from(*values)

        fun from(values: Map<String, String>): HttpParams = MutableHttpParams.from(values)

        fun fromQueryParams(uri: URI): HttpParams = MutableHttpParams.fromQueryParams(uri)
    }
}

class MutableHttpSingleParams private constructor(
    private val values: Map<String, String> = mutableMapOf(),
    private val lowercaseNames: Boolean = true,
    private val ignoreValueCase: Boolean = false,
    private val lowercaseValues: Boolean = false,
) : HttpSingleParams {
    override fun asMap() = values

    override fun get(name: String): String? {
        return values[normalizeName(name)]
    }

    override fun contains(name: String): Boolean {
        return get(name) != null
    }

    override fun contains(name: String, value: String): Boolean {
        val prevValue = get(name) ?: return false
        return prevValue.equals(value, ignoreCase = ignoreValueCase || lowercaseValues)
    }

    override fun with(other: HttpSingleParams, override: Boolean): HttpSingleParams {
        if (other.isEmpty()) return this
        val merged = mutableMapOf<String, String>()
        merged.putAll(values)
        var added = false
        other.forEach { key, value -> added = add(merged, key, value, override) || added }
        return if (added) MutableHttpSingleParams(merged) else this
    }

    override fun with(vararg pairs: Pair<String, String>, override: Boolean): HttpSingleParams {
        if (pairs.isEmpty()) return this
        val merged = mutableMapOf<String, String>()
        merged.putAll(values)
        var added = false
        pairs.forEach { it -> added = add(merged, it.first, it.second, override) || added }
        return if (added) MutableHttpSingleParams(merged) else this
    }

    private fun add(map: MutableMap<String, String>, name: String, value: String, override: Boolean): Boolean {
        val prevValue = get(name)
        if (prevValue == null) {
            map[name] = normalizeValue(value)
            return true
        }
        if (prevValue.equals(value, ignoreCase = ignoreValueCase || lowercaseValues) || !override) {
            return false
        }
        map[name] = normalizeValue(value)
        return true
    }

    override fun without(other: HttpSingleParams): HttpSingleParams {
        if (other.isEmpty()) return this
        val merged = mutableMapOf<String, String>()
        merged.putAll(values)
        var removed = false
        other.forEach { key, value -> removed = remove(merged, key, value) || removed }
        return if (removed) MutableHttpSingleParams(merged) else this
    }

    override fun without(vararg pairs: Pair<String, String>): HttpSingleParams {
        if (pairs.isEmpty()) return this
        val merged = mutableMapOf<String, String>()
        merged.putAll(values)
        var removed = false
        pairs.forEach { it -> removed = remove(merged, it.first, it.second) || removed }
        return if (removed) MutableHttpSingleParams(merged) else this
    }

    override fun without(name: String): HttpSingleParams {
        val normalizedName = normalizeName(name)
        if (!values.contains(normalizedName)) return this
        val merged = mutableMapOf<String, String>()
        merged.putAll(values)
        merged.remove(normalizedName)
        return MutableHttpSingleParams(merged)
    }

    private fun remove(map: MutableMap<String, String>, name: String, value: String): Boolean {
        val normalizedName = normalizeName(name)
        val prevValue = this.values[normalizedName] ?: return false
        if (!prevValue.equals(value, ignoreCase = ignoreValueCase || lowercaseValues)) {
            return false
        }
        map.remove(normalizedName)
        return true
    }

    private fun normalizeName(name: String): String {
        return if (lowercaseNames) {
            name.lowercase()
        } else {
            name
        }
    }

    private fun normalizeValue(value: String): String {
        return if (lowercaseValues) {
            value.lowercase()
        } else {
            value
        }
    }

    override fun toString(): String = values.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MutableHttpSingleParams) return false
        return values == other.values
    }

    override fun hashCode(): Int {
        return values.hashCode()
    }

    companion object {
        private val empty = MutableHttpSingleParams()

        fun empty(
            ignoreNameCase: Boolean = true,
            lowercaseNames: Boolean = true,
            ignoreValueCase: Boolean = false,
            lowercaseValues: Boolean = false,
            deduplicateEntries: Boolean = true,
        ) = empty

        fun from(vararg values: Pair<String, String>) = MutableHttpSingleParams(
            values.toMap(),
        )

        fun from(values: Map<String, String>) = MutableHttpSingleParams(values)

        fun fromQueryParams(uri: URI) = MutableHttpSingleParams(
            UriComponents.from(uri).queryParams,
        )
    }
}
