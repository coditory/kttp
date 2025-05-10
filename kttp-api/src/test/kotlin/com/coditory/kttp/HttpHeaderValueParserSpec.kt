package com.coditory.kttp

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.tuple
import io.kotest.matchers.shouldBe

class HttpHeaderValueParserSpec : FunSpec({
    context("should parse header value") {
        listOf(
            tuple("", null),
            tuple(" \t\n\r", null),
            tuple(" \t\n\r,,\n, ", null),
            tuple(" \t\n\r; ,,\n,; = ;;, ", null),
            tuple("text/html; charset=utf-8", HttpHeaderValue("text/html", HttpHeaderParams.from("charset" to "utf-8"))),
            tuple(
                "image/avif,image/webp,image/png,image/svg+xml,image/*;q=0.8,*/*;q=0.5",
                HttpHeaderValue(
                    HttpHeaderValueItem("image/avif"),
                    HttpHeaderValueItem("image/webp"),
                    HttpHeaderValueItem("image/png"),
                    HttpHeaderValueItem("image/svg+xml"),
                    HttpHeaderValueItem("image/*", HttpHeaderParams.from("q" to "0.8")),
                    HttpHeaderValueItem("*/*", HttpHeaderParams.from("q" to "0.5")),
                ),
            ),
            tuple(" Bearer 1231!@#!DFDR@# ", HttpHeaderValue("Bearer 1231!@#!DFDR@#")),
            tuple(
                "da, en-gb ",
                HttpHeaderValue(
                    HttpHeaderValueItem("da"),
                    HttpHeaderValueItem("en-gb"),
                ),
            ),
            tuple(
                "da, en-gb;q=0.8, en;q=0.7",
                HttpHeaderValue(
                    HttpHeaderValueItem("da"),
                    HttpHeaderValueItem("en-gb", HttpHeaderParams.from("q" to "0.8")),
                    HttpHeaderValueItem("en", HttpHeaderParams.from("q" to "0.7")),
                ),
            ),
            tuple(
                "attachment; filename=\"file name.jpg\"",
                HttpHeaderValue("attachment", HttpHeaderParams.from("filename" to "file name.jpg")),
            ),
            tuple(
                "attachment; filename=\"file name.jpg",
                HttpHeaderValue("attachment", HttpHeaderParams.from("filename" to "file name.jpg")),
            ),
            tuple(
                "attachment; filename= ",
                HttpHeaderValue("attachment"),
            ),
            tuple(
                "attachment;",
                HttpHeaderValue("attachment"),
            ),
        ).forEach { t ->
            val input = t.a
            val expected = t.b
            test("$input -> $expected") {
                val value = HttpHeaderValue.parse(input)
                value shouldBe expected
            }
        }
    }

    context("should serialize header value") {
        listOf(
            tuple("text/html; charset=utf-8", "text/html;charset=utf-8"),
            tuple(
                "image/avif,image/webp,image/png,image/svg+xml,image/*;q=0.8,*/*;q=0.5",
                "image/avif,image/webp,image/png,image/svg+xml,image/*;q=0.8,*/*;q=0.5",
            ),
            tuple(" Bearer 1231!@#!DFDR@# ", "Bearer 1231!@#!DFDR@#"),
            tuple("da, en-gb ", "da,en-gb"),
            tuple("da, en-gb;q=0.8, en;q=0.7", "da,en-gb;q=0.8,en;q=0.7"),
            tuple("attachment; filename=\"file name.jpg\"", "attachment;filename=\"file name.jpg\""),
            tuple("attachment; filename=\"file name.jpg", "attachment;filename=\"file name.jpg\""),
            tuple("attachment; filename= ", "attachment"),
            tuple("attachment;", "attachment"),
        ).forEach { t ->
            val input = t.a
            val expected = t.b
            test("$input -> $expected") {
                val parsed = HttpHeaderValue.parse(input)!!
                parsed.toHttpString() shouldBe expected
            }
        }
    }
})
