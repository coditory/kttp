package com.coditory.kttp

data class HttpStatus(
    val code: Int,
    val description: String,
) : Comparable<HttpStatus>, HttpSerializable {
    private val httpEntry = "$code $description"
    override fun toString() = httpEntry
    override fun toHttpString(builder: Appendable) {
        builder.append(httpEntry)
    }

    override fun equals(other: Any?): Boolean = other is HttpStatus && other.code == code

    override fun hashCode(): Int = code.hashCode()

    override fun compareTo(other: HttpStatus): Int = code - other.code

    fun isSuccess(): Boolean {
        return code >= 200 && code < 300
    }

    fun isRedirect(): Boolean {
        return code >= 300 && code < 400
    }

    fun isClientError(): Boolean {
        return code >= 400 && code < 500
    }

    fun isServerError(): Boolean {
        return code >= 500 && code < 600
    }

    companion object {
        val Continue: HttpStatus = HttpStatus(100, "Continue")
        val SwitchingProtocols: HttpStatus = HttpStatus(101, "Switching Protocols")
        val Processing: HttpStatus = HttpStatus(102, "Processing")
        val OK: HttpStatus = HttpStatus(200, "OK")
        val Created: HttpStatus = HttpStatus(201, "Created")
        val Accepted: HttpStatus = HttpStatus(202, "Accepted")
        val NonAuthoritativeInformation: HttpStatus =
            HttpStatus(203, "Non-Authoritative Information")
        val NoContent: HttpStatus = HttpStatus(204, "No Content")
        val ResetContent: HttpStatus = HttpStatus(205, "Reset Content")
        val PartialContent: HttpStatus = HttpStatus(206, "Partial Content")
        val MultiStatus: HttpStatus = HttpStatus(207, "Multi-Status")
        val MultipleChoices: HttpStatus = HttpStatus(300, "Multiple Choices")
        val MovedPermanently: HttpStatus = HttpStatus(301, "Moved Permanently")
        val Found: HttpStatus = HttpStatus(302, "Found")
        val SeeOther: HttpStatus = HttpStatus(303, "See Other")
        val NotModified: HttpStatus = HttpStatus(304, "Not Modified")
        val UseProxy: HttpStatus = HttpStatus(305, "Use Proxy")
        val SwitchProxy: HttpStatus = HttpStatus(306, "Switch Proxy")
        val TemporaryRedirect: HttpStatus = HttpStatus(307, "Temporary Redirect")
        val PermanentRedirect: HttpStatus = HttpStatus(308, "Permanent Redirect")
        val BadRequest: HttpStatus = HttpStatus(400, "Bad Request")
        val Unauthorized: HttpStatus = HttpStatus(401, "Unauthorized")
        val PaymentRequired: HttpStatus = HttpStatus(402, "Payment Required")
        val Forbidden: HttpStatus = HttpStatus(403, "Forbidden")
        val NotFound: HttpStatus = HttpStatus(404, "Not Found")
        val MethodNotAllowed: HttpStatus = HttpStatus(405, "Method Not Allowed")
        val NotAcceptable: HttpStatus = HttpStatus(406, "Not Acceptable")
        val ProxyAuthenticationRequired: HttpStatus =
            HttpStatus(407, "Proxy Authentication Required")
        val RequestTimeout: HttpStatus = HttpStatus(408, "Request Timeout")
        val Conflict: HttpStatus = HttpStatus(409, "Conflict")
        val Gone: HttpStatus = HttpStatus(410, "Gone")
        val LengthRequired: HttpStatus = HttpStatus(411, "Length Required")
        val PreconditionFailed: HttpStatus = HttpStatus(412, "Precondition Failed")
        val PayloadTooLarge: HttpStatus = HttpStatus(413, "Payload Too Large")
        val RequestURITooLong: HttpStatus = HttpStatus(414, "Request-URI Too Long")
        val UnsupportedMediaType: HttpStatus = HttpStatus(415, "Unsupported Media Type")
        val RequestedRangeNotSatisfiable: HttpStatus =
            HttpStatus(416, "Requested Range Not Satisfiable")
        val ExpectationFailed: HttpStatus = HttpStatus(417, "Expectation Failed")
        val UnprocessableEntity: HttpStatus = HttpStatus(422, "Unprocessable Entity")
        val Locked: HttpStatus = HttpStatus(423, "Locked")
        val FailedDependency: HttpStatus = HttpStatus(424, "Failed Dependency")
        val TooEarly: HttpStatus = HttpStatus(425, "Too Early")
        val UpgradeRequired: HttpStatus = HttpStatus(426, "Upgrade Required")
        val TooManyRequests: HttpStatus = HttpStatus(429, "Too Many Requests")
        val RequestHeaderFieldTooLarge: HttpStatus =
            HttpStatus(431, "Request Header Fields Too Large")
        val InternalServerError: HttpStatus = HttpStatus(500, "Internal Server Error")
        val NotImplemented: HttpStatus = HttpStatus(501, "Not Implemented")
        val BadGateway: HttpStatus = HttpStatus(502, "Bad Gateway")
        val ServiceUnavailable: HttpStatus = HttpStatus(503, "Service Unavailable")
        val GatewayTimeout: HttpStatus = HttpStatus(504, "Gateway Timeout")
        val VersionNotSupported: HttpStatus =
            HttpStatus(505, "HTTP Version Not Supported")
        val VariantAlsoNegotiates: HttpStatus = HttpStatus(506, "Variant Also Negotiates")
        val InsufficientStorage: HttpStatus = HttpStatus(507, "Insufficient Storage")

        private val statusCodes: Map<Int, HttpStatus> = listOf(
            Continue,
            SwitchingProtocols,
            Processing,
            OK,
            Created,
            Accepted,
            NonAuthoritativeInformation,
            NoContent,
            ResetContent,
            PartialContent,
            MultiStatus,
            MultipleChoices,
            MovedPermanently,
            Found,
            SeeOther,
            NotModified,
            UseProxy,
            SwitchProxy,
            TemporaryRedirect,
            PermanentRedirect,
            BadRequest,
            Unauthorized,
            PaymentRequired,
            Forbidden,
            NotFound,
            MethodNotAllowed,
            NotAcceptable,
            ProxyAuthenticationRequired,
            RequestTimeout,
            Conflict,
            Gone,
            LengthRequired,
            PreconditionFailed,
            PayloadTooLarge,
            RequestURITooLong,
            UnsupportedMediaType,
            RequestedRangeNotSatisfiable,
            ExpectationFailed,
            UnprocessableEntity,
            Locked,
            FailedDependency,
            TooEarly,
            UpgradeRequired,
            TooManyRequests,
            RequestHeaderFieldTooLarge,
            InternalServerError,
            NotImplemented,
            BadGateway,
            ServiceUnavailable,
            GatewayTimeout,
            VersionNotSupported,
            VariantAlsoNegotiates,
            InsufficientStorage,
        ).associateBy { it.code }

        fun from(code: Int): HttpStatus {
            return statusCodes[code] ?: HttpStatus(code, "Unknown Status Code")
        }
    }
}
