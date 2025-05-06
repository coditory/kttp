package com.coditory.ktserver.http

import com.coditory.quark.uri.UriComponents
import java.net.URI
import kotlin.collections.set

interface HttpParams {
    fun asMap(): Map<String, List<String>>

    operator fun get(name: String): String? = getAll(name)?.firstOrNull()

    fun getAll(name: String): List<String>?

    operator fun contains(name: String): Boolean

    fun contains(name: String, value: String): Boolean = contains(name, listOf(value))

    fun contains(name: String, values: List<String>): Boolean

    fun containsValue(value: String): Boolean

    fun names(): Set<String> = asMap().keys

    fun isEmpty(): Boolean = asMap().isEmpty()

    fun entries(): Set<Map.Entry<String, List<String>>> = asMap().entries

    operator fun plus(other: HttpParams): HttpParams

    operator fun minus(other: HttpParams): HttpParams

    fun with(
        other: HttpParams,
        overrideEntries: Boolean = false,
        allowDuplicates: Boolean = false,
    ): HttpParams

    fun with(
        name: String,
        value: String,
        overrideEntry: Boolean = false,
        allowDuplicates: Boolean = false,
    ): HttpParams

    fun with(
        name: String,
        values: List<String>,
        overrideEntry: Boolean = false,
        allowDuplicates: Boolean = false,
    ): HttpParams

    fun withMap(
        other: Map<String, String>,
        overrideEntries: Boolean = false,
        allowDuplicates: Boolean = false,
    ): HttpParams

    fun withMultiMap(
        other: Map<String, List<String>>,
        overrideEntries: Boolean = false,
        allowDuplicates: Boolean = false,
    ): HttpParams

    fun without(other: HttpParams): HttpParams

    fun without(vararg pairs: Pair<String, String>): HttpParams

    fun without(name: String, value: String): HttpParams

    fun withoutMap(other: Map<String, String>): HttpParams

    fun withoutMultiMap(other: Map<String, List<String>>): HttpParams

    fun without(name: String): HttpParams

    fun without(names: Collection<String>): HttpParams

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

    companion object {
        fun empty(): HttpParams = MutableHttpParams.empty()
        fun fromMap(values: Map<String, String>): HttpParams = MutableHttpParams.fromMap(values)
        fun fromMultiMap(values: Map<String, List<String>>): HttpParams = MutableHttpParams.fromMultiMap(values)
        fun parseQueryParams(uri: URI): HttpParams = MutableHttpParams.parseQueryParams(uri)
    }
}

class MutableHttpParams private constructor(
    private val values: MutableMap<String, List<String>> = mutableMapOf(),
    private val lowercaseNames: Boolean = true,
    private val ignoreValueCase: Boolean = false,
    private val lowercaseValues: Boolean = false,
) : HttpParams {
    fun copy(
        lowercaseNames: Boolean = this.lowercaseNames,
        ignoreValueCase: Boolean = this.ignoreValueCase,
        lowercaseValues: Boolean = this.lowercaseValues,
    ): MutableHttpParams {
        if (lowercaseNames != this.lowercaseNames || lowercaseValues != this.lowercaseValues) {
            val params = MutableHttpParams(
                lowercaseNames = lowercaseNames,
                ignoreValueCase = ignoreValueCase,
                lowercaseValues = lowercaseValues,
            )
            params.addMultiMap(values)
            return params
        }
        return MutableHttpParams(
            values = values,
            lowercaseNames = lowercaseNames,
            ignoreValueCase = ignoreValueCase,
            lowercaseValues = lowercaseValues,
        )
    }

    private fun copy(
        values: MutableMap<String, List<String>> = this.values,
        lowercaseNames: Boolean = this.lowercaseNames,
        ignoreValueCase: Boolean = this.ignoreValueCase,
        lowercaseValues: Boolean = this.lowercaseValues,
    ): MutableHttpParams {
        return MutableHttpParams(
            values = values,
            lowercaseNames = lowercaseNames,
            ignoreValueCase = ignoreValueCase,
            lowercaseValues = lowercaseValues,
        )
    }

    override fun asMap() = values

    override fun getAll(name: String): List<String>? {
        return values[normalizeName(name)]
    }

    override fun contains(name: String): Boolean {
        return getAll(name) != null
    }

    override fun contains(name: String, values: List<String>): Boolean {
        val found = getAll(name) ?: return false
        if (values.isEmpty()) return true
        return values.all { value ->
            found.any {
                it.equals(value, ignoreCase = ignoreValueCase || lowercaseValues)
            }
        }
    }

    override fun containsValue(value: String): Boolean {
        return this.values.any { entry ->
            entry.value.any {
                it.equals(value, ignoreCase = lowercaseValues || ignoreValueCase)
            }
        }
    }

    override operator fun plus(other: HttpParams): MutableHttpParams = with(other)

    override operator fun minus(other: HttpParams): MutableHttpParams = without(other)

    override fun with(
        other: HttpParams,
        overrideEntries: Boolean,
        allowDuplicates: Boolean,
    ) = withMultiMap(other.asMap(), overrideEntries, allowDuplicates)

    override fun with(
        name: String,
        value: String,
        overrideEntry: Boolean,
        allowDuplicates: Boolean,
    ) = withMultiMap(mapOf(name to listOf(value)), overrideEntry, allowDuplicates)

    override fun with(
        name: String,
        values: List<String>,
        overrideEntry: Boolean,
        allowDuplicates: Boolean,
    ) = withMultiMap(mapOf(name to values), overrideEntry, allowDuplicates)

    override fun withMap(
        other: Map<String, String>,
        overrideEntries: Boolean,
        allowDuplicates: Boolean,
    ): MutableHttpParams = withMultiMap(toMultiMap(other), overrideEntries, allowDuplicates)

    override fun withMultiMap(
        other: Map<String, List<String>>,
        overrideEntries: Boolean,
        allowDuplicates: Boolean,
    ): MutableHttpParams {
        if (other.isEmpty()) return this
        val merged = mutableMapOf<String, List<String>>()
        merged.putAll(this.values)
        val copy = copy(merged)
        val added = copy.addMultiMap(other, overrideEntries, allowDuplicates)
        return if (added) copy else this
    }

    fun add(
        other: HttpParams,
        overrideEntries: Boolean = false,
        allowDuplicates: Boolean = false,
    ) = addMultiMap(other.asMap(), overrideEntries, allowDuplicates)

    fun add(
        name: String,
        value: String,
        overrideEntries: Boolean = false,
        allowDuplicates: Boolean = false,
    ) = addMultiMap(mapOf(name to listOf(value)), overrideEntries, allowDuplicates)

    fun add(
        name: String,
        values: List<String>,
        overrideEntries: Boolean = false,
        allowDuplicates: Boolean = false,
    ) = addMultiMap(mapOf(name to values), overrideEntries, allowDuplicates)

    fun addMap(
        other: Map<String, String>,
        overrideEntries: Boolean = false,
        allowDuplicates: Boolean = false,
    ) = addMultiMap(toMultiMap(other), overrideEntries, allowDuplicates)

    fun addMultiMap(
        other: Map<String, List<String>>,
        overrideEntries: Boolean = false,
        allowDuplicates: Boolean = false,
    ): Boolean {
        if (other.isEmpty()) return false
        val merged = this.values
        if (overrideEntries) {
            other.forEach { name, values ->
                merged.put(normalizeName(name), normalizeValues(values))
            }
            return true
        }
        var added = false
        other.forEach { key, values -> added = addEntry(merged, key, values, allowDuplicates) || added }
        return added
    }

    private fun addEntry(
        map: MutableMap<String, List<String>>,
        name: String,
        values: List<String>,
        allowDuplicates: Boolean,
    ): Boolean {
        if (values.isEmpty()) return false
        val prevValues = getAll(name)
        val normalizedValues = normalizeValues(values)
        if (prevValues == null) {
            map[name] = normalizedValues
        } else if (!allowDuplicates) {
            val filteredValues = normalizedValues.filter { value ->
                prevValues.none { it.equals(value, ignoreCase = ignoreValueCase) }
            }
            if (filteredValues.isEmpty()) {
                return false
            }
            map[name] = values + filteredValues
        } else {
            map[name] = values + normalizedValues
        }
        return true
    }

    override fun without(other: HttpParams) = withMultiMap(other.asMap())

    override fun without(vararg pairs: Pair<String, String>) = withoutMultiMap(pairs.groupBy({ it.first }, { it.second }))

    override fun without(name: String, value: String) = withoutMultiMap(mapOf(name to listOf(value)))

    override fun withoutMap(other: Map<String, String>) = withoutMultiMap(toMultiMap(other))

    override fun withoutMultiMap(other: Map<String, List<String>>): MutableHttpParams {
        if (other.isEmpty()) return this
        val merged = mutableMapOf<String, List<String>>()
        merged.putAll(values)
        val copy = copy(merged)
        val removed = copy.removeMultiMap(other)
        return if (removed) copy else this
    }

    override fun without(names: Collection<String>): MutableHttpParams {
        if (names.isEmpty()) return this
        val merged = mutableMapOf<String, List<String>>()
        merged.putAll(values)
        var removed = false
        names
            .map(this::normalizeName)
            .forEach { removed = merged.remove(it) != null || removed }
        return if (removed) copy(merged) else this
    }

    override fun without(name: String): MutableHttpParams {
        val normalizedName = normalizeName(name)
        if (!values.contains(normalizedName)) return this
        val merged = mutableMapOf<String, List<String>>()
        merged.putAll(values)
        merged.remove(normalizedName)
        return copy(merged)
    }

    fun remove(name: String): Boolean {
        val normalizedName = normalizeName(name)
        return this.values.remove(normalizedName) != null
    }

    fun remove(names: Collection<String>): Boolean {
        var removed = false
        names.forEach { removed = remove(it) || removed }
        return removed
    }

    fun remove(other: HttpParams): Boolean = removeMultiMap(other.asMap())

    fun remove(name: String, value: String) = removeMultiMap(mapOf(name to listOf(value)))

    fun remove(name: String, values: List<String>) = removeMultiMap(mapOf(name to values))

    fun removeMap(other: Map<String, String>) = removeMultiMap(toMultiMap(other))

    fun removeMultiMap(other: Map<String, List<String>>): Boolean {
        if (other.isEmpty()) return false
        val merged = this.values
        var removed = false
        other.forEach { key, values -> removed = removeEntry(merged, key, values) || removed }
        return removed
    }

    private fun removeEntry(map: MutableMap<String, List<String>>, name: String, values: List<String>): Boolean {
        if (values.isEmpty()) return false
        val normalizedName = normalizeName(name)
        val prevValues = this.values[normalizedName] ?: return false
        val filtered = values.filter { value ->
            prevValues.none { it.equals(value, ignoreCase = ignoreValueCase || lowercaseValues) }
        }
        if (filtered.isEmpty()) {
            map.remove(normalizedName)
        } else {
            map[name] = filtered
        }
        return true
    }

    private fun normalizeName(name: String): String {
        return if (lowercaseNames) {
            name.lowercase()
        } else {
            name
        }
    }

    private fun toMultiMap(other: Map<String, String>): Map<String, List<String>> {
        return other
            .map { it.key to listOf(it.value) }
            .toMap()
    }

    private fun normalizeValues(values: List<String>): List<String> {
        return values.map(this::normalizeValue)
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
        if (other !is MutableHttpParams) return false
        return values == other.values
    }

    override fun hashCode(): Int {
        return values.hashCode()
    }

    companion object {
        private val EMPTY = MutableHttpParams()

        fun empty(): MutableHttpParams = EMPTY
        fun fromMap(values: Map<String, String>): MutableHttpParams = EMPTY.withMap(values)
        fun fromMultiMap(values: Map<String, List<String>>): MutableHttpParams = EMPTY.withMultiMap(values)

        fun parseQueryParams(uri: URI): MutableHttpParams = fromMultiMap(
            UriComponents.from(uri).queryMultiParams,
        )

        fun parseHeaderParameters(header: String): MutableHttpParams {
            val idx = header.indexOf(";")
            if (idx < 0) return empty()
            val paramsChunk = header.substring(idx, header.length).trim()
            if (paramsChunk.isEmpty()) return empty()
            val params = paramsChunk.split(";")
                .map {
                    val chunks = it.split("=")
                    val key = chunks[0].trim()
                    val value = chunks[1].trim()
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        key to value.substring(1, value.length - 1)
                    } else {
                        key to value
                    }
                }
                .filter { it.first.isNotBlank() && it.second.isNotBlank() }
                .groupBy({ it.first }, { it.second })
            return fromMultiMap(params)
        }
    }
}
