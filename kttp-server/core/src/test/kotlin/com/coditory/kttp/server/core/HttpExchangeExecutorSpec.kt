package com.coditory.kttp.server.core

import com.coditory.kttp.HttpRequestMethod.Companion.DELETE
import com.coditory.kttp.HttpRequestMethod.Companion.GET
import com.coditory.kttp.HttpRequestMethod.Companion.HEAD
import com.coditory.kttp.HttpRequestMethod.Companion.OPTIONS
import com.coditory.kttp.HttpRequestMethod.Companion.PATCH
import com.coditory.kttp.HttpRequestMethod.Companion.POST
import com.coditory.kttp.HttpRequestMethod.Companion.PUT
import com.coditory.kttp.HttpStatus
import com.coditory.kttp.server.HttpRequest
import com.coditory.kttp.server.HttpResponse.TextResponse
import com.coditory.kttp.server.core.base.InMemServer
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class HttpExchangeExecutorSpec : FunSpec({
    test("should route request by HTTP method") {
        val server = InMemServer()
        val path = "/test"
        server.routing {
            get(path) { TextResponse("Handler for GET") }
            post(path) { TextResponse("Handler for POST") }
            put(path) { TextResponse("Handler for PUT") }
            patch(path) { TextResponse("Handler for PATCH") }
            delete(path) { TextResponse("Handler for DELETE") }
            head(path) { TextResponse("Handler for HEAD") }
            options(path) { TextResponse("Handler for OPTIONS") }
        }
        server.handle(HttpRequest(GET, path)) shouldBe TextResponse("Handler for GET")
        server.handle(HttpRequest(POST, path)) shouldBe TextResponse("Handler for POST")
        server.handle(HttpRequest(PUT, path)) shouldBe TextResponse("Handler for PUT")
        server.handle(HttpRequest(PATCH, path)) shouldBe TextResponse("Handler for PATCH")
        server.handle(HttpRequest(DELETE, path)) shouldBe TextResponse("Handler for DELETE")
        server.handle(HttpRequest(HEAD, path)) shouldBe TextResponse("Handler for HEAD")
        server.handle(HttpRequest(OPTIONS, path)) shouldBe TextResponse("Handler for OPTIONS")
    }

    test("should route request by path") {
        val server = InMemServer()
        server.routing {
            get("/") { TextResponse("Handler for /") }
            get("/foobar") { TextResponse("Handler for /foobar") }
            get("/foo") { TextResponse("Handler for /foo") }
            get("/foo/foo") { TextResponse("Handler for /foo/foo") }
            get("/foo/foo/foo") { TextResponse("Handler for /foo/foo/foo") }
        }
        server.handle(HttpRequest(GET, "/")) shouldBe TextResponse("Handler for /")
        server.handle(HttpRequest(GET, "/foobar")) shouldBe TextResponse("Handler for /foobar")
        server.handle(HttpRequest(GET, "/foo")) shouldBe TextResponse("Handler for /foo")
        server.handle(HttpRequest(GET, "/foo/foo")) shouldBe TextResponse("Handler for /foo/foo")
        server.handle(HttpRequest(GET, "/foo/foo/foo")) shouldBe TextResponse("Handler for /foo/foo/foo")
        server.handle(HttpRequest(GET, "/foobaz")).status shouldBe HttpStatus.NotFound
        server.handle(HttpRequest(GET, "/fooo")).status shouldBe HttpStatus.NotFound
        server.handle(HttpRequest(GET, "/fo")).status shouldBe HttpStatus.NotFound
        server.handle(HttpRequest(GET, "/foo/foo/foo/foo")).status shouldBe HttpStatus.NotFound
    }

    test("should route request by path with *") {
        val server = InMemServer()
        server.routing {
            get("/*") { TextResponse("Handler for /*") }
            get("/foo") { TextResponse("Handler for /foo") }
            get("/foo/*") { TextResponse("Handler for /foo/*") }
            get("/foo/*/baz") { TextResponse("Handler for /foo/*/baz") }
            get("/bar/*") { TextResponse("Handler for /bar/*") }
        }
        server.handle(HttpRequest(GET, "/fo")) shouldBe TextResponse("Handler for /*")
        server.handle(HttpRequest(GET, "/foo")) shouldBe TextResponse("Handler for /foo")
        server.handle(HttpRequest(GET, "/foo/bar")) shouldBe TextResponse("Handler for /foo/*")
        server.handle(HttpRequest(GET, "/foo/bar/baz")) shouldBe TextResponse("Handler for /foo/*/baz")
        server.handle(HttpRequest(GET, "/bar/bar")) shouldBe TextResponse("Handler for /bar/*")
        server.handle(HttpRequest(GET, "/bar")) shouldBe TextResponse("Handler for /*")
        server.handle(HttpRequest(GET, "/")).status shouldBe HttpStatus.NotFound
        server.handle(HttpRequest(GET, "/foo/foo/foo")).status shouldBe HttpStatus.NotFound
        server.handle(HttpRequest(GET, "/bar/bar/bar")).status shouldBe HttpStatus.NotFound
    }

    test("should route request by path with **") {
        val server = InMemServer()
        server.routing {
            get("/**") { TextResponse("Handler for /**") }
            get("/foo") { TextResponse("Handler for /foo") }
            get("/foo/**") { TextResponse("Handler for /foo/**") }
            get("/foo/**/baz") { TextResponse("Handler for /foo/**/baz") }
            get("/bar/**/baz") { TextResponse("Handler for /bar/**") }
        }
        server.handle(HttpRequest(GET, "/fo")) shouldBe TextResponse("Handler for /**")
        server.handle(HttpRequest(GET, "/foo")) shouldBe TextResponse("Handler for /foo")
        server.handle(HttpRequest(GET, "/foo/bar")) shouldBe TextResponse("Handler for /foo/**")
        server.handle(HttpRequest(GET, "/foo/foo/foo")) shouldBe TextResponse("Handler for /foo/**")
        // server.handle(HttpRequest(GET, "/foo/bar/baz")) shouldBe TextResponse("Handler for /foo/**/baz")
        server.handle(HttpRequest(GET, "/bar/bar/baz")) shouldBe TextResponse("Handler for /bar/**")
        server.handle(HttpRequest(GET, "/bar/bar/bar")) shouldBe TextResponse("Handler for /**")
        server.handle(HttpRequest(GET, "/bar")) shouldBe TextResponse("Handler for /**")
        server.handle(HttpRequest(GET, "/")).status shouldBe HttpStatus.NotFound
    }
})
