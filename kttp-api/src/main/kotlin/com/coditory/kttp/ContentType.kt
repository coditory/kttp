package com.coditory.kttp

import java.nio.charset.Charset
import kotlin.collections.iterator

class ContentType private constructor(
    val contentType: String,
    val contentSubtype: String,
    val parameters: HttpParams = HttpParams.empty(),
) {
    val value by lazy { "$contentType/$contentSubtype" }

    fun withParameter(name: String, value: String): ContentType {
        if (parameters.contains(name, value)) return this
        return ContentType(contentType, contentSubtype, parameters.with(name, value, overrideEntry = true))
    }

    private fun hasParameter(name: String, value: String): Boolean {
        return parameters.contains(name, value)
    }

    fun withoutParameter(name: String): ContentType {
        if (!parameters.contains(name)) return this
        return ContentType(contentType, contentSubtype, parameters.without(name))
    }

    fun withoutParameter(name: String, value: String): ContentType {
        if (!parameters.contains(name, value)) return this
        return ContentType(contentType, contentSubtype, parameters.without(name, value))
    }

    fun withoutParameters(): ContentType = when {
        parameters.isEmpty() -> this
        else -> ContentType(contentType, contentSubtype)
    }

    /**
     * Checks if `this` type matches a [pattern] type taking into account placeholder symbols `*` and parameters.
     * The `this` type must be a more specific type than the [pattern] type. In other words:
     *
     * ```kotlin
     * ContentType("a", "b").match(ContentType("a", "b").withParameter("foo", "bar")) === false
     * ContentType("a", "b").withParameter("foo", "bar").match(ContentType("a", "b")) === true
     * ContentType("a", "*").match(ContentType("a", "b")) === false
     * ContentType("a", "b").match(ContentType("a", "*")) === true
     * ```
     */
    fun match(pattern: ContentType): Boolean {
        if (pattern.contentType != "*" && !pattern.contentType.equals(contentType, ignoreCase = true)) {
            return false
        }
        if (pattern.contentSubtype != "*" && !pattern.contentSubtype.equals(contentSubtype, ignoreCase = true)) {
            return false
        }
        for ((patternName, patternValues) in pattern.parameters.asMap()) {
            val matches = patternValues.all { patternValue ->
                when (patternName) {
                    "*" -> {
                        when (patternValue) {
                            "*" -> true
                            else -> parameters.containsValue(patternValue)
                        }
                    }

                    else -> {
                        when (patternValue) {
                            "*" -> parameters.contains(patternName)
                            else -> parameters.contains(patternName, patternValue)
                        }
                    }
                }
            }
            if (!matches) {
                return false
            }
        }
        return true
    }

//    fun match(pattern: String): Boolean {
//        return match(parse(pattern))
//    }

    fun withCharset(charset: Charset): ContentType = withParameter("charset", charset.name())

    fun charsetName(): String? {
        return parameters["charset"] ?: return null
    }

    fun charset(): Charset? {
        val name = charsetName()
        return try {
            Charset.forName(name)
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is ContentType &&
            contentType.equals(other.contentType, ignoreCase = true) &&
            contentSubtype.equals(other.contentSubtype, ignoreCase = true) &&
            parameters == other.parameters
    }

    override fun hashCode(): Int {
        var result = contentType.lowercase().hashCode()
        result += 31 * result + contentSubtype.lowercase().hashCode()
        result += 31 * parameters.hashCode()
        return result
    }

    companion object {
//        fun parse(value: String): ContentType {
//            if (value.isBlank()) return Any
//            return parse(value) { parts, parameters ->
//                val slash = parts.indexOf('/')
//                if (slash == -1) {
//                    if (parts.trim() == "*") return Any
//                    throw BadContentTypeFormatException(value)
//                }
//                val type = parts.substring(0, slash).trim()
//                if (type.isEmpty()) {
//                    throw BadContentTypeFormatException(value)
//                }
//                val subtype = parts.substring(slash + 1).trim()
//                if (type.contains(' ') || subtype.contains(' ')) {
//                    throw BadContentTypeFormatException(value)
//                }
//                if (subtype.isEmpty() || subtype.contains('/')) {
//                    throw BadContentTypeFormatException(value)
//                }
//                ContentType(type, subtype, parameters)
//            }
//        }

        val Any: ContentType = ContentType("*", "*")
    }

    object Application {
        const val TYPE: String = "application"
        val Any: ContentType = ContentType(TYPE, "*")
        val Atom: ContentType = ContentType(TYPE, "atom+xml")
        val Cbor: ContentType = ContentType(TYPE, "cbor")
        val Json: ContentType = ContentType(TYPE, "json")
        val HalJson: ContentType = ContentType(TYPE, "hal+json")
        val JavaScript: ContentType = ContentType(TYPE, "javascript")
        val OctetStream: ContentType = ContentType(TYPE, "octet-stream")
        val Rss: ContentType = ContentType(TYPE, "rss+xml")
        val Soap: ContentType = ContentType(TYPE, "soap+xml")
        val Xml: ContentType = ContentType(TYPE, "xml")
        val Xml_Dtd: ContentType = ContentType(TYPE, "xml-dtd")
        val Yaml: ContentType = ContentType(TYPE, "yaml")
        val Zip: ContentType = ContentType(TYPE, "zip")
        val GZip: ContentType = ContentType(TYPE, "gzip")
        val FormUrlEncoded: ContentType = ContentType(TYPE, "x-www-form-urlencoded")
        val Pdf: ContentType = ContentType(TYPE, "pdf")
        val Xlsx: ContentType = ContentType(TYPE, "vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        val Docx: ContentType = ContentType(TYPE, "vnd.openxmlformats-officedocument.wordprocessingml.document")
        val Pptx: ContentType =
            ContentType(TYPE, "vnd.openxmlformats-officedocument.presentationml.presentation")
        val ProtoBuf: ContentType = ContentType(TYPE, "protobuf")
        val Wasm: ContentType = ContentType(TYPE, "wasm")
        val ProblemJson: ContentType = ContentType(TYPE, "problem+json")
        val ProblemXml: ContentType = ContentType(TYPE, "problem+xml")

        operator fun contains(contentType: CharSequence): Boolean = contentType.startsWith("$TYPE/", ignoreCase = true)
        operator fun contains(contentType: ContentType): Boolean = contentType.match(Any)
    }

    object Audio {
        const val TYPE: String = "audio"
        val Any: ContentType = ContentType(TYPE, "*")
        val MP4: ContentType = ContentType(TYPE, "mp4")
        val MPEG: ContentType = ContentType(TYPE, "mpeg")
        val OGG: ContentType = ContentType(TYPE, "ogg")

        operator fun contains(contentType: CharSequence): Boolean = contentType.startsWith("$TYPE/", ignoreCase = true)
        operator fun contains(contentType: ContentType): Boolean = contentType.match(Any)
    }

    object Image {
        const val TYPE: String = "image"
        val Any: ContentType = ContentType(TYPE, "*")
        val GIF: ContentType = ContentType(TYPE, "gif")
        val JPEG: ContentType = ContentType(TYPE, "jpeg")
        val PNG: ContentType = ContentType(TYPE, "png")
        val SVG: ContentType = ContentType(TYPE, "svg+xml")
        val XIcon: ContentType = ContentType(TYPE, "x-icon")

        operator fun contains(contentSubtype: String): Boolean = contentSubtype.startsWith("$TYPE/", ignoreCase = true)
        operator fun contains(contentType: ContentType): Boolean = contentType.match(Any)
    }

    object Message {
        const val TYPE: String = "message"
        val Any: ContentType = ContentType(TYPE, "*")
        val Http: ContentType = ContentType(TYPE, "http")

        operator fun contains(contentSubtype: String): Boolean = contentSubtype.startsWith("$TYPE/", ignoreCase = true)
        operator fun contains(contentType: ContentType): Boolean = contentType.match(Any)
    }

    object MultiPart {
        const val TYPE: String = "multipart"
        val Any: ContentType = ContentType(TYPE, "*")
        val Mixed: ContentType = ContentType(TYPE, "mixed")
        val Alternative: ContentType = ContentType(TYPE, "alternative")
        val Related: ContentType = ContentType(TYPE, "related")
        val FormData: ContentType = ContentType(TYPE, "form-data")
        val Signed: ContentType = ContentType(TYPE, "signed")
        val Encrypted: ContentType = ContentType(TYPE, "encrypted")
        val ByteRanges: ContentType = ContentType(TYPE, "byteranges")

        operator fun contains(contentType: CharSequence): Boolean = contentType.startsWith("$TYPE/", ignoreCase = true)
        operator fun contains(contentType: ContentType): Boolean = contentType.match(Any)
    }

    object Text {
        const val TYPE: String = "text"
        val Any: ContentType = ContentType(TYPE, "*")
        val Plain: ContentType = ContentType(TYPE, "plain")
        val CSS: ContentType = ContentType(TYPE, "css")
        val CSV: ContentType = ContentType(TYPE, "csv")
        val Html: ContentType = ContentType(TYPE, "html")
        val JavaScript: ContentType = ContentType(TYPE, "javascript")
        val VCard: ContentType = ContentType(TYPE, "vcard")
        val Xml: ContentType = ContentType(TYPE, "xml")
        val EventStream: ContentType = ContentType(TYPE, "event-stream")

        operator fun contains(contentType: CharSequence): Boolean = contentType.startsWith("$TYPE/", ignoreCase = true)
        operator fun contains(contentType: ContentType): Boolean = contentType.match(Any)
    }

    object Video {
        const val TYPE: String = "video"
        val Any: ContentType = ContentType(TYPE, "*")
        val MPEG: ContentType = ContentType(TYPE, "mpeg")
        val MP4: ContentType = ContentType(TYPE, "mp4")
        val OGG: ContentType = ContentType(TYPE, "ogg")
        val QuickTime: ContentType = ContentType(TYPE, "quicktime")

        operator fun contains(contentType: CharSequence): Boolean = contentType.startsWith("$TYPE/", ignoreCase = true)
        operator fun contains(contentType: ContentType): Boolean = contentType.match(Any)
    }

    object Font {
        const val TYPE: String = "font"
        val Any: ContentType = ContentType(TYPE, "*")
        val Collection: ContentType = ContentType(TYPE, "collection")
        val Otf: ContentType = ContentType(TYPE, "otf")
        val Sfnt: ContentType = ContentType(TYPE, "sfnt")
        val Ttf: ContentType = ContentType(TYPE, "ttf")
        val Woff: ContentType = ContentType(TYPE, "woff")
        val Woff2: ContentType = ContentType(TYPE, "woff2")

        operator fun contains(contentType: CharSequence): Boolean = contentType.startsWith("$TYPE/", ignoreCase = true)
        operator fun contains(contentType: ContentType): Boolean = contentType.match(Any)
    }
}

class BadContentTypeFormatException(value: String) : Exception("Bad Content-Type format: $value")
