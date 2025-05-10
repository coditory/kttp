package com.coditory.kttp

enum class HttpRequestMethod : HttpSerializable {
    GET,
    POST,
    PUT,
    DELETE,
    HEAD,
    OPTIONS,
    OTHER,
    ;

    override fun toHttpString(builder: Appendable) {
        builder.append(this.name)
    }

    companion object {
        fun parse(text: String): HttpRequestMethod {
            return when (text.uppercase()) {
                GET.name -> GET
                POST.name -> POST
                PUT.name -> PUT
                DELETE.name -> DELETE
                HEAD.name -> HEAD
                OPTIONS.name -> OPTIONS
                else -> OTHER
            }
        }
    }
}
