package com.coditory.kttp.headers

import com.coditory.kttp.HttpParams
import com.coditory.kttp.HttpSerializable
import com.coditory.kttp.MutableHttpParams
import com.coditory.kttp.headers.MutableHttpHeaderParams.Companion.headerParamValueToHttpString
import com.coditory.kttp.toMultiMap

interface HttpHeaderParams : HttpParams, HttpSerializable {
    override fun toHttpString(builder: Appendable) {
        if (isEmpty()) return
        forEachEntry { name, value ->
            builder.append(';')
            builder.append(name)
            builder.append('=')
            headerParamValueToHttpString(builder, value)
        }
    }

    override operator fun plus(other: HttpParams): HttpHeaderParams

    override operator fun minus(other: HttpParams): HttpHeaderParams

    override fun with(
        other: HttpParams,
        overrideEntries: Boolean,
        allowDuplicates: Boolean,
    ): HttpHeaderParams

    override fun with(
        name: String,
        value: String,
        overrideEntry: Boolean,
        allowDuplicates: Boolean,
    ): HttpHeaderParams

    override fun with(
        name: String,
        values: List<String>,
        overrideEntry: Boolean,
        allowDuplicates: Boolean,
    ): HttpHeaderParams

    override fun withMap(
        other: Map<String, String>,
        overrideEntries: Boolean,
        allowDuplicates: Boolean,
    ): HttpHeaderParams

    override fun withMultiMap(
        other: Map<String, List<String>>,
        overrideEntries: Boolean,
        allowDuplicates: Boolean,
    ): HttpHeaderParams

    override fun without(other: HttpParams): HttpHeaderParams

    override fun without(vararg pairs: Pair<String, String>): HttpHeaderParams

    override fun without(name: String, value: String): HttpHeaderParams

    override fun withoutMap(other: Map<String, String>): HttpHeaderParams

    override fun withoutMultiMap(other: Map<String, List<String>>): HttpHeaderParams

    override fun without(names: Collection<String>): HttpHeaderParams

    override fun without(name: String): HttpHeaderParams

    companion object {
        private val EMPTY = MutableHttpHeaderParams()

        fun empty(): HttpHeaderParams = EMPTY
        fun from(vararg values: Pair<String, String>): HttpHeaderParams = fromMap(values.toMap())
        fun fromMap(values: Map<String, String>): HttpHeaderParams = MutableHttpHeaderParams.fromMap(values)
        fun fromMultiMap(values: Map<String, List<String>>): HttpHeaderParams = MutableHttpHeaderParams.fromMultiMap(values)
    }
}

class MutableHttpHeaderParams private constructor(
    private val params: MutableHttpParams,
) : MutableHttpParams, HttpHeaderParams {
    constructor() : this(MutableHttpParams.Companion.empty())

    override fun asMap() = params.asMap()

    override fun getAll(name: String) = params.getAll(name)

    override fun contains(name: String, values: List<String>) = params.contains(name, values)

    override fun containsValue(value: String) = params.containsValue(value)

    override fun plus(other: HttpParams): MutableHttpHeaderParams = with(other)

    override fun minus(other: HttpParams): MutableHttpHeaderParams = without(other)

    override fun with(
        other: HttpParams,
        overrideEntries: Boolean,
        allowDuplicates: Boolean,
    ): MutableHttpHeaderParams = withMultiMap(other.asMap(), overrideEntries, allowDuplicates)

    override fun with(
        name: String,
        value: String,
        overrideEntry: Boolean,
        allowDuplicates: Boolean,
    ): MutableHttpHeaderParams = withMultiMap(mapOf(name to listOf(value)), overrideEntry, allowDuplicates)

    override fun with(
        name: String,
        values: List<String>,
        overrideEntry: Boolean,
        allowDuplicates: Boolean,
    ): MutableHttpHeaderParams = withMultiMap(mapOf(name to values), overrideEntry, allowDuplicates)

    override fun withMap(
        other: Map<String, String>,
        overrideEntries: Boolean,
        allowDuplicates: Boolean,
    ): MutableHttpHeaderParams = withMultiMap(toMultiMap(other), overrideEntries, allowDuplicates)

    override fun withMultiMap(
        other: Map<String, List<String>>,
        overrideEntries: Boolean,
        allowDuplicates: Boolean,
    ): MutableHttpHeaderParams {
        val merged = params.withMultiMap(other, overrideEntries, allowDuplicates)
        return if (merged === params) this else MutableHttpHeaderParams(merged)
    }

    override fun without(other: HttpParams): MutableHttpHeaderParams = withoutMultiMap(other.asMap())

    override fun without(vararg pairs: Pair<String, String>) = withoutMultiMap(pairs.groupBy({ it.first }, { it.second }))

    override fun without(name: String, value: String) = withoutMultiMap(mapOf(name to listOf(value)))

    override fun withoutMap(other: Map<String, String>) = withoutMultiMap(toMultiMap(other))

    override fun withoutMultiMap(other: Map<String, List<String>>): MutableHttpHeaderParams {
        val result = params.withoutMultiMap(other)
        return if (result === this.params) this else MutableHttpHeaderParams(result)
    }

    override fun without(name: String): MutableHttpHeaderParams = without(setOf(name))

    override fun without(names: Collection<String>): MutableHttpHeaderParams {
        val result = params.without(names)
        return if (result === this.params) this else MutableHttpHeaderParams(result)
    }

    override fun addMultiMap(other: Map<String, List<String>>, allowDuplicates: Boolean): Boolean {
        return this.params.addMultiMap(other, allowDuplicates)
    }

    override fun setMultiMap(other: Map<String, List<String>>): Boolean {
        return this.params.setMultiMap(other)
    }

    override fun remove(names: Collection<String>): Boolean {
        return this.params.remove(names)
    }

    override fun removeMultiMap(other: Map<String, List<String>>): Boolean {
        return this.params.removeMultiMap(other)
    }

    override fun toString(): String = params.toString()

    override fun equals(other: Any?) = params == other

    override fun hashCode(): Int = params.hashCode()

    companion object {
        fun empty() = MutableHttpHeaderParams()
        fun from(vararg values: Pair<String, String>) = fromMap(values.toMap())
        fun fromMap(values: Map<String, String>) = MutableHttpHeaderParams().apply { addMap(values) }
        fun fromMultiMap(values: Map<String, List<String>>) = MutableHttpHeaderParams().apply { addMultiMap(values) }

        private val quoteRegex = Regex("\"")

        internal fun headerParamValueToHttpString(builder: Appendable, value: CharSequence) {
            if (value.contains(',') || value.contains(';') || value.contains('\'') || value.contains('"') || value.contains(' ')) {
                builder.append('"')
                if (value.contains('"')) {
                    builder.append(value.replace(quoteRegex, "\\\""))
                } else {
                    builder.append(value)
                }
                builder.append('"')
            } else {
                builder.append(value)
            }
        }
    }
}
