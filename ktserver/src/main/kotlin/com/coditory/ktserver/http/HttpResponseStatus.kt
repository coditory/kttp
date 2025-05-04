package com.coditory.ktserver.http

data class HttpResponseStatus(
    val code: Int,
    val description: String,
) : Comparable<HttpResponseStatus> {
    fun toHttpString(): String = "$code $description"

    override fun equals(other: Any?): Boolean = other is HttpResponseStatus && other.code == code

    override fun hashCode(): Int = code.hashCode()

    override fun compareTo(other: HttpResponseStatus): Int = code - other.code

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
        val Continue: HttpResponseStatus = HttpResponseStatus(100, "Continue")
        val SwitchingProtocols: HttpResponseStatus = HttpResponseStatus(101, "Switching Protocols")
        val Processing: HttpResponseStatus = HttpResponseStatus(102, "Processing")
        val OK: HttpResponseStatus = HttpResponseStatus(200, "OK")
        val Created: HttpResponseStatus = HttpResponseStatus(201, "Created")
        val Accepted: HttpResponseStatus = HttpResponseStatus(202, "Accepted")
        val NonAuthoritativeInformation: HttpResponseStatus =
            HttpResponseStatus(203, "Non-Authoritative Information")
        val NoContent: HttpResponseStatus = HttpResponseStatus(204, "No Content")
        val ResetContent: HttpResponseStatus = HttpResponseStatus(205, "Reset Content")
        val PartialContent: HttpResponseStatus = HttpResponseStatus(206, "Partial Content")
        val MultiStatus: HttpResponseStatus = HttpResponseStatus(207, "Multi-Status")
        val MultipleChoices: HttpResponseStatus = HttpResponseStatus(300, "Multiple Choices")
        val MovedPermanently: HttpResponseStatus = HttpResponseStatus(301, "Moved Permanently")
        val Found: HttpResponseStatus = HttpResponseStatus(302, "Found")
        val SeeOther: HttpResponseStatus = HttpResponseStatus(303, "See Other")
        val NotModified: HttpResponseStatus = HttpResponseStatus(304, "Not Modified")
        val UseProxy: HttpResponseStatus = HttpResponseStatus(305, "Use Proxy")
        val SwitchProxy: HttpResponseStatus = HttpResponseStatus(306, "Switch Proxy")
        val TemporaryRedirect: HttpResponseStatus = HttpResponseStatus(307, "Temporary Redirect")
        val PermanentRedirect: HttpResponseStatus = HttpResponseStatus(308, "Permanent Redirect")
        val BadRequest: HttpResponseStatus = HttpResponseStatus(400, "Bad Request")
        val Unauthorized: HttpResponseStatus = HttpResponseStatus(401, "Unauthorized")
        val PaymentRequired: HttpResponseStatus = HttpResponseStatus(402, "Payment Required")
        val Forbidden: HttpResponseStatus = HttpResponseStatus(403, "Forbidden")
        val NotFound: HttpResponseStatus = HttpResponseStatus(404, "Not Found")
        val MethodNotAllowed: HttpResponseStatus = HttpResponseStatus(405, "Method Not Allowed")
        val NotAcceptable: HttpResponseStatus = HttpResponseStatus(406, "Not Acceptable")
        val ProxyAuthenticationRequired: HttpResponseStatus =
            HttpResponseStatus(407, "Proxy Authentication Required")
        val RequestTimeout: HttpResponseStatus = HttpResponseStatus(408, "Request Timeout")
        val Conflict: HttpResponseStatus = HttpResponseStatus(409, "Conflict")
        val Gone: HttpResponseStatus = HttpResponseStatus(410, "Gone")
        val LengthRequired: HttpResponseStatus = HttpResponseStatus(411, "Length Required")
        val PreconditionFailed: HttpResponseStatus = HttpResponseStatus(412, "Precondition Failed")
        val PayloadTooLarge: HttpResponseStatus = HttpResponseStatus(413, "Payload Too Large")
        val RequestURITooLong: HttpResponseStatus = HttpResponseStatus(414, "Request-URI Too Long")
        val UnsupportedMediaType: HttpResponseStatus = HttpResponseStatus(415, "Unsupported Media Type")
        val RequestedRangeNotSatisfiable: HttpResponseStatus =
            HttpResponseStatus(416, "Requested Range Not Satisfiable")
        val ExpectationFailed: HttpResponseStatus = HttpResponseStatus(417, "Expectation Failed")
        val UnprocessableEntity: HttpResponseStatus = HttpResponseStatus(422, "Unprocessable Entity")
        val Locked: HttpResponseStatus = HttpResponseStatus(423, "Locked")
        val FailedDependency: HttpResponseStatus = HttpResponseStatus(424, "Failed Dependency")
        val TooEarly: HttpResponseStatus = HttpResponseStatus(425, "Too Early")
        val UpgradeRequired: HttpResponseStatus = HttpResponseStatus(426, "Upgrade Required")
        val TooManyRequests: HttpResponseStatus = HttpResponseStatus(429, "Too Many Requests")
        val RequestHeaderFieldTooLarge: HttpResponseStatus =
            HttpResponseStatus(431, "Request Header Fields Too Large")
        val InternalServerError: HttpResponseStatus = HttpResponseStatus(500, "Internal Server Error")
        val NotImplemented: HttpResponseStatus = HttpResponseStatus(501, "Not Implemented")
        val BadGateway: HttpResponseStatus = HttpResponseStatus(502, "Bad Gateway")
        val ServiceUnavailable: HttpResponseStatus = HttpResponseStatus(503, "Service Unavailable")
        val GatewayTimeout: HttpResponseStatus = HttpResponseStatus(504, "Gateway Timeout")
        val VersionNotSupported: HttpResponseStatus =
            HttpResponseStatus(505, "HTTP Version Not Supported")
        val VariantAlsoNegotiates: HttpResponseStatus = HttpResponseStatus(506, "Variant Also Negotiates")
        val InsufficientStorage: HttpResponseStatus = HttpResponseStatus(507, "Insufficient Storage")

        private val statusCodes: Map<Int, HttpResponseStatus> = listOf(
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

        fun fromValue(code: Int): HttpResponseStatus {
            return statusCodes[code] ?: HttpResponseStatus(code, "Unknown Status Code")
        }
    }
}
