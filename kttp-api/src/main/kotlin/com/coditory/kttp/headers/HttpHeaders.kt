package com.coditory.kttp.headers

import com.coditory.kttp.HttpParams
import com.coditory.kttp.HttpSerializable
import com.coditory.kttp.MutableHttpParams
import com.coditory.kttp.toMultiMap

interface HttpHeaders : HttpParams, HttpSerializable {
    fun contentType(): ContentType?
    fun accept(): ContentType?

    override fun toHttpString(builder: Appendable) {
        forEachEntry { key, value ->
            builder.append(key).append(": ").append(value).append("\r\n")
        }
        builder.append("\r\n")
    }

    override operator fun plus(other: HttpParams): HttpHeaders

    override operator fun minus(other: HttpParams): HttpHeaders

    override fun with(
        other: HttpParams,
        overrideEntries: Boolean,
        allowDuplicates: Boolean,
    ): HttpHeaders

    override fun with(
        name: String,
        value: String,
        overrideEntry: Boolean,
        allowDuplicates: Boolean,
    ): HttpHeaders

    override fun with(
        name: String,
        values: List<String>,
        overrideEntry: Boolean,
        allowDuplicates: Boolean,
    ): HttpHeaders

    override fun withMap(
        other: Map<String, String>,
        overrideEntries: Boolean,
        allowDuplicates: Boolean,
    ): HttpHeaders

    override fun withMultiMap(
        other: Map<String, List<String>>,
        overrideEntries: Boolean,
        allowDuplicates: Boolean,
    ): HttpHeaders

    override fun without(other: HttpParams): HttpHeaders

    override fun without(vararg pairs: Pair<String, String>): HttpHeaders

    override fun without(name: String, value: String): HttpHeaders

    override fun withoutMap(other: Map<String, String>): HttpHeaders

    override fun withoutMultiMap(other: Map<String, List<String>>): HttpHeaders

    override fun without(names: Collection<String>): HttpHeaders

    override fun without(name: String): HttpHeaders

    @Suppress("unused", "MayBeConstant")
    companion object {
        private val EMPTY = MutableHttpHeaders()

        fun empty(): HttpHeaders = EMPTY
        fun from(vararg values: Pair<String, String>) = fromMap(values.toMap())
        fun fromMap(values: Map<String, String>): HttpHeaders = MutableHttpHeaders.fromMap(values)
        fun fromMultiMap(values: Map<String, List<String>>): HttpHeaders = MutableHttpHeaders.fromMultiMap(values)

        // Permanently registered standard HTTP headers
        // The list is taken from http://www.iana.org/assignments/message-headers/message-headers.xml#perm-headers
        val Accept: String = "Accept"
        val AcceptCharset: String = "Accept-Charset"
        val AcceptEncoding: String = "Accept-Encoding"
        val AcceptLanguage: String = "Accept-Language"
        val AcceptRanges: String = "Accept-Ranges"
        val Age: String = "Age"
        val Allow: String = "Allow"

        // Application-Layer Protocol Negotiation, HTTP/2
        val ALPN: String = "ALPN"
        val AuthenticationInfo: String = "Authentication-Info"
        val Authorization: String = "Authorization"
        val CacheControl: String = "Cache-Control"
        val Connection: String = "Connection"
        val ContentDisposition: String = "Content-Disposition"
        val ContentEncoding: String = "Content-Encoding"
        val ContentLanguage: String = "Content-Language"
        val ContentLength: String = "Content-Length"
        val ContentLocation: String = "Content-Location"
        val ContentRange: String = "Content-Range"
        val ContentType: String = "Content-Type"
        val Cookie: String = "Cookie"

        // WebDAV Search
        val DASL: String = "DASL"
        val Date: String = "Date"

        // WebDAV
        val DAV: String = "DAV"
        val Depth: String = "Depth"

        val Destination: String = "Destination"
        val ETag: String = "ETag"
        val Expect: String = "Expect"
        val Expires: String = "Expires"
        val From: String = "From"
        val Forwarded: String = "Forwarded"
        val Host: String = "Host"
        val HTTP2Settings: String = "HTTP2-Settings"
        val If: String = "If"
        val IfMatch: String = "If-Match"
        val IfModifiedSince: String = "If-Modified-Since"
        val IfNoneMatch: String = "If-None-Match"
        val IfRange: String = "If-Range"
        val IfScheduleTagMatch: String = "If-Schedule-Tag-Match"
        val IfUnmodifiedSince: String = "If-Unmodified-Since"
        val LastModified: String = "Last-Modified"
        val Location: String = "Location"
        val LockToken: String = "Lock-Token"
        val Link: String = "Link"
        val MaxForwards: String = "Max-Forwards"
        val MIMEVersion: String = "MIME-Version"
        val OrderingType: String = "Ordering-Type"
        val Origin: String = "Origin"
        val Overwrite: String = "Overwrite"
        val Position: String = "Position"
        val Pragma: String = "Pragma"
        val Prefer: String = "Prefer"
        val PreferenceApplied: String = "Preference-Applied"
        val ProxyAuthenticate: String = "Proxy-Authenticate"
        val ProxyAuthenticationInfo: String = "Proxy-Authentication-Info"
        val ProxyAuthorization: String = "Proxy-Authorization"
        val PublicKeyPins: String = "Public-Key-Pins"
        val PublicKeyPinsReportOnly: String = "Public-Key-Pins-Report-Only"
        val Range: String = "Range"
        val Referrer: String = "Referer"
        val RetryAfter: String = "Retry-After"
        val ScheduleReply: String = "Schedule-Reply"
        val ScheduleTag: String = "Schedule-Tag"
        val SecWebSocketAccept: String = "Sec-WebSocket-Accept"
        val SecWebSocketExtensions: String = "Sec-WebSocket-Extensions"
        val SecWebSocketKey: String = "Sec-WebSocket-Key"
        val SecWebSocketProtocol: String = "Sec-WebSocket-Protocol"
        val SecWebSocketVersion: String = "Sec-WebSocket-Version"
        val Server: String = "Server"
        val SetCookie: String = "Set-Cookie"

        // Atom Publishing
        val SLUG: String = "SLUG"
        val StrictTransportSecurity: String = "Strict-Transport-Security"
        val TE: String = "TE"
        val Timeout: String = "Timeout"
        val Trailer: String = "Trailer"
        val TransferEncoding: String = "Transfer-Encoding"
        val Upgrade: String = "Upgrade"
        val UserAgent: String = "User-Agent"
        val Vary: String = "Vary"
        val Via: String = "Via"
        val Warning: String = "Warning"
        val WWWAuthenticate: String = "WWW-Authenticate"

        // CORS
        val AccessControlAllowOrigin: String = "Access-Control-Allow-Origin"
        val AccessControlAllowMethods: String = "Access-Control-Allow-Methods"
        val AccessControlAllowCredentials: String = "Access-Control-Allow-Credentials"
        val AccessControlAllowHeaders: String = "Access-Control-Allow-Headers"

        val AccessControlRequestMethod: String = "Access-Control-Request-Method"
        val AccessControlRequestHeaders: String = "Access-Control-Request-Headers"
        val AccessControlExposeHeaders: String = "Access-Control-Expose-Headers"
        val AccessControlMaxAge: String = "Access-Control-Max-Age"

        // Unofficial de-facto headers
        val XHttpMethodOverride: String = "X-Http-Method-Override"
        val XForwardedHost: String = "X-Forwarded-Host"
        val XForwardedServer: String = "X-Forwarded-Server"
        val XForwardedProto: String = "X-Forwarded-Proto"
        val XForwardedFor: String = "X-Forwarded-For"
        val XForwardedPort: String = "X-Forwarded-Port"
        val XRequestId: String = "X-Request-ID"
        val XCorrelationId: String = "X-Correlation-ID"
        val XTotalCount: String = "X-Total-Count"
    }
}

class MutableHttpHeaders private constructor(
    private val params: MutableHttpParams,
) : MutableHttpParams, HttpHeaders {
    constructor() : this(MutableHttpParams.Companion.empty())

    private var cache = mutableMapOf<String, Any?>()

    private fun getParsed(key: String, parse: (v: String) -> Any?): Any? {
        return cache.getOrPut(key) {
            val value = get(HttpHeaders.ContentType)
            if (value == null) {
                null
            } else {
                parse(value) as Any
            }
        }
    }

    private fun setParsed(key: String, value: String?) {
        if (value == null) {
            remove(key)
            cache.remove(key)
        } else {
            set(key, value)
            cache.put(key, value)
        }
    }

    override fun contentType(): ContentType? {
        return getParsed(HttpHeaders.ContentType, ContentType::parse)
            ?.let { it as ContentType }
    }

    fun contentType(value: ContentType?) {
        setParsed(HttpHeaders.ContentType, value?.value)
    }

    override fun accept(): ContentType? {
        return getParsed(HttpHeaders.Accept, ContentType::parse)
            ?.let { it as ContentType }
    }

    fun accept(value: ContentType?) {
        setParsed(HttpHeaders.Accept, value?.value)
    }

    override fun toMap() = params.toMap()

    override fun getAll(name: String) = params.getAll(name)

    override fun contains(name: String, values: List<String>) = params.contains(name, values)

    override fun containsValue(value: String) = params.containsValue(value)

    override fun plus(other: HttpParams): MutableHttpHeaders = withMultiMap(other.toMap())

    override fun minus(other: HttpParams): MutableHttpHeaders = withoutMultiMap(other.toMap())

    override fun with(
        other: HttpParams,
        overrideEntries: Boolean,
        allowDuplicates: Boolean,
    ): MutableHttpHeaders = withMultiMap(other.toMap(), overrideEntries, allowDuplicates)

    override fun with(
        name: String,
        value: String,
        overrideEntry: Boolean,
        allowDuplicates: Boolean,
    ): MutableHttpHeaders = withMultiMap(mapOf(name to listOf(value)), overrideEntry, allowDuplicates)

    override fun with(
        name: String,
        values: List<String>,
        overrideEntry: Boolean,
        allowDuplicates: Boolean,
    ): MutableHttpHeaders = withMultiMap(mapOf(name to values), overrideEntry, allowDuplicates)

    override fun withMap(
        other: Map<String, String>,
        overrideEntries: Boolean,
        allowDuplicates: Boolean,
    ): MutableHttpHeaders = withMultiMap(toMultiMap(other), overrideEntries, allowDuplicates)

    override fun withMultiMap(
        other: Map<String, List<String>>,
        overrideEntries: Boolean,
        allowDuplicates: Boolean,
    ): MutableHttpHeaders {
        val merged = params.withMultiMap(other, overrideEntries, allowDuplicates)
        return if (merged === params) this else MutableHttpHeaders(merged)
    }

    override fun without(other: HttpParams): MutableHttpHeaders = withoutMultiMap(other.toMap())

    override fun without(vararg pairs: Pair<String, String>) = withoutMultiMap(pairs.groupBy({ it.first }, { it.second }))

    override fun without(name: String, value: String) = withoutMultiMap(mapOf(name to listOf(value)))

    override fun withoutMap(other: Map<String, String>) = withoutMultiMap(toMultiMap(other))

    override fun withoutMultiMap(other: Map<String, List<String>>): MutableHttpHeaders {
        val result = params.withoutMultiMap(other)
        return if (result === this.params) this else MutableHttpHeaders(result)
    }

    override fun without(name: String): MutableHttpHeaders = without(setOf(name))

    override fun without(names: Collection<String>): MutableHttpHeaders {
        val result = params.without(names)
        return if (result === this.params) this else MutableHttpHeaders(result)
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
        fun empty() = MutableHttpHeaders()
        fun from(vararg values: Pair<String, String>) = fromMap(values.toMap())
        fun fromMap(values: Map<String, String>) = MutableHttpHeaders().apply { addMap(values) }
        fun fromMultiMap(values: Map<String, List<String>>) = MutableHttpHeaders().apply { addMultiMap(values) }
    }
}
