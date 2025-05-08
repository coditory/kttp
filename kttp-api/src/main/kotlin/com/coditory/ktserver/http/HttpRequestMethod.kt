package com.coditory.ktserver.http

enum class HttpRequestMethod {
    GET,
    POST,
    PUT,
    DELETE,
    HEAD,
    OPTIONS,
    OTHER,
    ;

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
