package com.coditory.kttp.headers

import com.coditory.kttp.HttpSerializable

class HttpHeaderValue(
    items: List<HttpHeaderValueItem>,
) : HttpSerializable {
    constructor(
        value: CharSequence,
        params: HttpHeaderParams = HttpHeaderParams.empty(),
    ) : this(listOf(HttpHeaderValueItem(value, params)))

    constructor(
        vararg items: HttpHeaderValueItem,
    ) : this(items.toList())

    override fun toHttpString(builder: Appendable) {
        for (i in 0..items.size - 1) {
            if (i > 0) builder.append(',')
            items[i].toHttpString(builder)
        }
    }

    val items = items.sorted()

    fun with(other: HttpHeaderValue): HttpHeaderValue {
        return HttpHeaderValue(items + other.items)
    }

    override fun toString(): String {
        return this::class.simpleName + items.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as HttpHeaderValue
        return items == other.items
    }

    override fun hashCode(): Int {
        return items.hashCode()
    }

    companion object {
        fun parse(header: CharSequence) = HttpHeaderValueParser.parse(header)
    }
}

data class HttpHeaderValueItem(
    val value: CharSequence,
    val params: HttpHeaderParams = HttpHeaderParams.empty(),
) : HttpSerializable, Comparable<HttpHeaderValueItem> {
    override fun toHttpString(builder: Appendable) {
        headerValueToHttpString(builder, value)
        params.toHttpString(builder)
    }

    fun quality(): Float {
        return params["q"]?.toFloatOrNull() ?: 1.0f
    }

    override fun compareTo(other: HttpHeaderValueItem): Int {
        return this.quality().compareTo(other.quality())
    }

    companion object {
        private val quoteRegex = Regex("\"")
        internal fun headerValueToHttpString(builder: Appendable, value: CharSequence) {
            if (value.contains(',') || value.contains(';') || value.contains('\'') || value.contains('"')) {
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

private object HttpHeaderValueParser {
    private val separators = setOf(',', ';')

    fun parse(header: CharSequence): HttpHeaderValue? {
        val trimmed = header.trim()
        if (trimmed.isEmpty()) return null
        return parseValue(HttpHeaderParsingBuffer(header.trim()))
    }

    private fun parseValue(buffer: HttpHeaderParsingBuffer): HttpHeaderValue? {
        val items = mutableListOf<HttpHeaderValueItem>()
        var item = parseItem(buffer)
        while (item != null) {
            items.add(item)
            item = parseItem(buffer)
        }
        return if (items.isEmpty()) null else HttpHeaderValue(items)
    }

    private fun parseItem(buffer: HttpHeaderParsingBuffer): HttpHeaderValueItem? {
        if (buffer.isEmpty()) return null
        buffer.skipWhitespaces()
        val c = buffer.char() ?: return null
        val value = if (c == '"' || c == '\'') {
            buffer.quotedValue()
        } else {
            buffer.valueUntil(separators).trimEnd()
        }
        val c2 = buffer.char()
            ?: return if (value.isEmpty()) null else HttpHeaderValueItem(value)
        val params = if (c2 == ';') {
            buffer.inc()
            parseParameters(buffer)
        } else {
            HttpHeaderParams.empty()
        }
        buffer.inc()
        return if (value.isEmpty() && params.isEmpty()) null else HttpHeaderValueItem(value, params)
    }

    private fun parseParameters(buffer: HttpHeaderParsingBuffer): HttpHeaderParams {
        if (buffer.isEmpty()) return HttpHeaderParams.empty()
        val result = MutableHttpHeaderParams()
        buffer.skipWhitespaces()
        while (!buffer.isEmpty() && buffer.char() != ',') {
            val name = buffer.valueUntil('=').trimEnd()
            buffer.inc()
            buffer.skipWhitespaces()
            if (buffer.isEmpty()) return HttpHeaderParams.empty()
            val c = buffer.char()
            val value = if (c == '"' || c == '\'') {
                buffer.quotedValue()
            } else {
                buffer.valueUntil(separators).trimEnd()
            }
            if (buffer.char() == ';') {
                buffer.inc()
            }
            buffer.skipWhitespaces()
            result.add(name.toString(), value.toString())
        }
        return result
    }
}

private class HttpHeaderParsingBuffer(
    private val seq: CharSequence,
) {
    private var idx = 0

    fun isEmpty() = idx >= seq.length

    fun skipWhitespaces() {
        while (!isEmpty() && Character.isWhitespace(seq[idx])) inc()
    }

    fun char(): Char? {
        if (isEmpty()) return null
        return seq[idx]
    }

    fun quotedValue(): CharSequence {
        if (isEmpty()) return ""
        val q = seq[idx]
        inc()
        if (isEmpty()) return ""
        val start = idx
        while (!isEmpty() && seq[idx] != q) inc()
        val result = seq.subSequence(start, idx)
        if (!isEmpty() && q == seq[idx]) {
            inc()
        }
        return result
    }

    fun valueUntil(sep: Char): CharSequence {
        return valueUntil(setOf(sep))
    }

    fun valueUntil(sep: Set<Char>): CharSequence {
        if (idx == seq.length - 1) return ""
        val start = idx
        while (idx < seq.length && !sep.contains(seq[idx])) inc()
        val result = seq.subSequence(start, idx)
        return result
    }

    fun inc() {
        if (idx < seq.length) idx++
    }
}
