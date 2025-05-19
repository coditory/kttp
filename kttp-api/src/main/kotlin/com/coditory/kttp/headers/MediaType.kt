package com.coditory.kttp.headers

import com.coditory.kttp.HttpSerializable
import java.nio.charset.Charset

data class MediaType(
    val type: String,
    val subtype: String,
    val parameters: HttpHeaderParams = HttpHeaderParams.empty(),
) : HttpSerializable {
    val value by lazy { "$type/$subtype" + parameters.toHttpString() }

    override fun toString() = toHttpString()

    override fun toHttpString(builder: Appendable) {
        builder.append(value)
    }

    fun quality(): Float? {
        return parameters["q"]?.toFloatOrNull()
    }

    fun withQuality(quality: Float?): MediaType {
        return if (quality() == quality) this else withoutParameter("q")
    }

    fun withParameter(name: String, value: String?): MediaType {
        if (value == null) return withoutParameter(name)
        if (parameters.contains(name, value)) return this
        return MediaType(type, subtype, parameters.with(name, value, overrideEntry = true))
    }

    private fun hasParameter(name: String, value: String): Boolean {
        return parameters.contains(name, value)
    }

    fun withoutParameter(name: String): MediaType {
        if (!parameters.contains(name)) return this
        return MediaType(type, subtype, parameters.without(name))
    }

    fun withoutParameter(name: String, value: String): MediaType {
        if (!parameters.contains(name, value)) return this
        return MediaType(type, subtype, parameters.without(name, value))
    }

    fun withoutParameters(): MediaType = when {
        parameters.isEmpty() -> this
        else -> MediaType(type, subtype)
    }

    fun withCharset(charset: Charset): MediaType = withParameter("charset", charset.name())

    fun charsetName(): String? {
        return parameters["charset"]
    }

    fun charset(): Charset? {
        val name = charsetName()
        return try {
            Charset.forName(name)
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    fun contains(other: MediaType, ignoreParams: Boolean = false): Boolean {
        if (type != "*" && !type.equals(other.type, ignoreCase = true)) {
            return false
        }
        if (subtype != "*" && !subtype.equals(other.subtype, ignoreCase = true)) {
            if (!other.subtype.contains('+') || subtype.contains('+')) {
                return false
            }
            if (!other.subtype.endsWith("+$subtype", ignoreCase = true)) {
                return false
            }
        }
        if (ignoreParams) return true
        for ((paramName, paramValues) in parameters.toMap()) {
            val mediaParams = other.parameters.getAll(paramName)?.toSet() ?: emptySet()
            if (!mediaParams.containsAll(paramValues)) {
                return false
            }
        }
        return true
    }

    companion object {
        fun parse(value: String): MediaType? {
            val header = HttpHeaderValue.parse(value) ?: return null
            if (header.items.size != 1) throw BadMediaTypeFormatException(value)
            val item = header.items.first()
            return parse(item)
        }

        fun parse(item: HttpHeaderValueItem): MediaType? {
            val parts = item.value
            val slash = parts.indexOf('/')
            if (slash == -1) {
                throw BadMediaTypeFormatException(item.toHttpString())
            }
            val type = parts.substring(0, slash).trim()
            if (type.isEmpty()) {
                throw BadMediaTypeFormatException(item.toHttpString())
            }
            val subtype = parts.substring(slash + 1).trim()
            if (type.contains(' ') || subtype.contains(' ')) {
                throw BadMediaTypeFormatException(item.toHttpString())
            }
            if (subtype.isEmpty() || subtype.contains('/')) {
                throw BadMediaTypeFormatException(item.toHttpString())
            }
            return MediaType(type, subtype, item.params)
        }

        fun minimize(contentTypes: Collection<MediaType>, ignoreParams: Boolean = false): Set<MediaType> {
            val result = mutableSetOf<MediaType>()
            val types = if (ignoreParams) {
                contentTypes.map { it.withoutParameters() }.toList()
            } else {
                contentTypes.toList()
            }
            for (i in 0 until contentTypes.size) {
                val ci = types[i]
                var skip = false
                for (j in i + 1 until contentTypes.size) {
                    val cj = types[j]
                    if (cj.contains(ci)) {
                        skip = true
                        break
                    }
                }
                if (!skip) {
                    result.add(ci)
                }
            }
            return result
        }
    }

    object Application {
        const val TYPE: String = "application"
        val Any: MediaType = MediaType(TYPE, "*")
        val Atom: MediaType = MediaType(TYPE, "atom+xml")
        val Cbor: MediaType = MediaType(TYPE, "cbor")
        val Json: MediaType = MediaType(TYPE, "json")
        val HalJson: MediaType = MediaType(TYPE, "hal+json")
        val JavaScript: MediaType = MediaType(TYPE, "javascript")
        val OctetStream: MediaType = MediaType(TYPE, "octet-stream")
        val Rss: MediaType = MediaType(TYPE, "rss+xml")
        val Soap: MediaType = MediaType(TYPE, "soap+xml")
        val Xml: MediaType = MediaType(TYPE, "xml")
        val Xml_Dtd: MediaType = MediaType(TYPE, "xml-dtd")
        val Yaml: MediaType = MediaType(TYPE, "yaml")
        val Zip: MediaType = MediaType(TYPE, "zip")
        val GZip: MediaType = MediaType(TYPE, "gzip")
        val FormUrlEncoded: MediaType = MediaType(TYPE, "x-www-form-urlencoded")
        val Pdf: MediaType = MediaType(TYPE, "pdf")
        val Xlsx: MediaType = MediaType(TYPE, "vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        val Docx: MediaType = MediaType(TYPE, "vnd.openxmlformats-officedocument.wordprocessingml.document")
        val Pptx: MediaType =
            MediaType(TYPE, "vnd.openxmlformats-officedocument.presentationml.presentation")
        val ProtoBuf: MediaType = MediaType(TYPE, "protobuf")
        val Wasm: MediaType = MediaType(TYPE, "wasm")
        val ProblemJson: MediaType = MediaType(TYPE, "problem+json")
        val ProblemXml: MediaType = MediaType(TYPE, "problem+xml")

        fun subtype(subtype: String) = MediaType(TYPE, subtype)
        operator fun contains(contentType: CharSequence): Boolean = contentType.startsWith("$TYPE/", ignoreCase = true)
        operator fun contains(mediaType: MediaType): Boolean = mediaType.type.equals(TYPE, ignoreCase = true)
    }

    object Audio {
        const val TYPE: String = "audio"
        val Any: MediaType = MediaType(TYPE, "*")
        val MP4: MediaType = MediaType(TYPE, "mp4")
        val MPEG: MediaType = MediaType(TYPE, "mpeg")
        val OGG: MediaType = MediaType(TYPE, "ogg")

        fun subtype(subtype: String) = MediaType(TYPE, subtype)
        operator fun contains(contentType: CharSequence): Boolean = contentType.startsWith("$TYPE/", ignoreCase = true)
        operator fun contains(mediaType: MediaType): Boolean = mediaType.type.equals(TYPE, ignoreCase = true)
    }

    object Image {
        const val TYPE: String = "image"
        val Any: MediaType = MediaType(TYPE, "*")
        val GIF: MediaType = MediaType(TYPE, "gif")
        val JPEG: MediaType = MediaType(TYPE, "jpeg")
        val PNG: MediaType = MediaType(TYPE, "png")
        val SVG: MediaType = MediaType(TYPE, "svg+xml")
        val XIcon: MediaType = MediaType(TYPE, "x-icon")

        operator fun contains(contentSubtype: String): Boolean = contentSubtype.startsWith("$TYPE/", ignoreCase = true)
        operator fun contains(mediaType: MediaType): Boolean = mediaType.type.equals(TYPE, ignoreCase = true)
    }

    object Message {
        const val TYPE: String = "message"
        val Any: MediaType = MediaType(TYPE, "*")
        val Http: MediaType = MediaType(TYPE, "http")

        fun subtype(subtype: String) = MediaType(TYPE, subtype)
        operator fun contains(contentSubtype: String): Boolean = contentSubtype.startsWith("$TYPE/", ignoreCase = true)
        operator fun contains(mediaType: MediaType): Boolean = mediaType.type.equals(TYPE, ignoreCase = true)
    }

    object MultiPart {
        const val TYPE: String = "multipart"
        val Any: MediaType = MediaType(TYPE, "*")
        val Mixed: MediaType = MediaType(TYPE, "mixed")
        val Alternative: MediaType = MediaType(TYPE, "alternative")
        val Related: MediaType = MediaType(TYPE, "related")
        val FormData: MediaType = MediaType(TYPE, "form-data")
        val Signed: MediaType = MediaType(TYPE, "signed")
        val Encrypted: MediaType = MediaType(TYPE, "encrypted")
        val ByteRanges: MediaType = MediaType(TYPE, "byteranges")

        fun subtype(subtype: String) = MediaType(TYPE, subtype)
        operator fun contains(contentType: CharSequence): Boolean = contentType.startsWith("$TYPE/", ignoreCase = true)
        operator fun contains(mediaType: MediaType): Boolean = mediaType.type.equals(TYPE, ignoreCase = true)
    }

    object Text {
        const val TYPE: String = "text"
        val Any: MediaType = MediaType(TYPE, "*")
        val Plain: MediaType = MediaType(TYPE, "plain")
        val CSS: MediaType = MediaType(TYPE, "css")
        val CSV: MediaType = MediaType(TYPE, "csv")
        val Html: MediaType = MediaType(TYPE, "html")
        val JavaScript: MediaType = MediaType(TYPE, "javascript")
        val VCard: MediaType = MediaType(TYPE, "vcard")
        val Xml: MediaType = MediaType(TYPE, "xml")
        val EventStream: MediaType = MediaType(TYPE, "event-stream")

        fun subtype(subtype: String) = MediaType(TYPE, subtype)
        operator fun contains(contentType: CharSequence): Boolean = contentType.startsWith("$TYPE/", ignoreCase = true)
        operator fun contains(mediaType: MediaType): Boolean = mediaType.type.equals(TYPE, ignoreCase = true)
    }

    object Video {
        const val TYPE: String = "video"
        val Any: MediaType = MediaType(TYPE, "*")
        val MPEG: MediaType = MediaType(TYPE, "mpeg")
        val MP4: MediaType = MediaType(TYPE, "mp4")
        val OGG: MediaType = MediaType(TYPE, "ogg")
        val QuickTime: MediaType = MediaType(TYPE, "quicktime")

        fun subtype(subtype: String) = MediaType(TYPE, subtype)
        operator fun contains(contentType: CharSequence): Boolean = contentType.startsWith("$TYPE/", ignoreCase = true)
        operator fun contains(mediaType: MediaType): Boolean = mediaType.type.equals(TYPE, ignoreCase = true)
    }

    object Font {
        const val TYPE: String = "font"
        val Any: MediaType = MediaType(TYPE, "*")
        val Collection: MediaType = MediaType(TYPE, "collection")
        val Otf: MediaType = MediaType(TYPE, "otf")
        val Sfnt: MediaType = MediaType(TYPE, "sfnt")
        val Ttf: MediaType = MediaType(TYPE, "ttf")
        val Woff: MediaType = MediaType(TYPE, "woff")
        val Woff2: MediaType = MediaType(TYPE, "woff2")

        fun subtype(subtype: String) = MediaType(TYPE, subtype)
        operator fun contains(mediaType: CharSequence): Boolean = mediaType.startsWith("$TYPE/", ignoreCase = true)
        operator fun contains(mediaType: MediaType): Boolean = mediaType.type.equals(TYPE, ignoreCase = true)
    }
}

class BadMediaTypeFormatException(value: String) : Exception("Bad Media-Type format: $value")
