package com.coditory.kttp.headers

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.tuple
import io.kotest.matchers.shouldBe

class ContentTypeSpec : FunSpec({
    context("should parse ContentType") {
        listOf(
            tuple("", null),
            tuple(" \t\n\r; ,,\n,; = ;;, ", null),
            tuple(" application / json ", ContentType.Application.Json),
            tuple("text/html; charset=utf-8", ContentType.Text.Html.withParameter("charset", "utf-8")),
            tuple("image/*;q=0.8", ContentType.Image.Any.withParameter("q", "0.8")),
        ).forEach { t ->
            val input = t.a
            val expected = t.b
            test("$input -> $expected") {
                val value = ContentType.parse(input)
                value shouldBe expected
            }
        }
    }

    context("should serialize ContentType") {
        listOf(
            tuple("application/json", "application/json"),
            tuple("text/html; charset=utf-8", "text/html;charset=utf-8"),
        ).forEach { t ->
            val input = t.a
            val expected = t.b
            test("$input -> $expected") {
                val parsed = ContentType.parse(input)!!
                parsed.toHttpString() shouldBe expected
            }
        }
    }

    context("should minimize ContentTypes") {
        listOf(
            tuple(listOf("application/json", "application/*", "text/html", "*/html"), setOf("application/*", "*/html")),
            tuple(listOf("application/json", "application/*", "text/html", "*/*"), setOf("*/*")),
            tuple(listOf("application/json;q=0.8", "application/*;charset=utf-8"), setOf("application/json;q=0.8", "application/*;charset=utf-8")),
            tuple(listOf("application/json;charset=utf-8", "application/*;charset=utf-8"), setOf("application/*;charset=utf-8")),
        ).forEach { t ->
            val input = t.a.mapNotNull { ContentType.parse(it) }
            val expected = t.b.map { ContentType.parse(it) }.toSet()
            test("$input -> $expected") {
                val parsed = ContentType.minimize(input)
                parsed shouldBe expected
            }
        }
    }

    context("should minimize ContentTypes ignoring parameters") {
        listOf(
            tuple(listOf("application/json", "application/*", "text/html", "*/html"), setOf("application/*", "*/html")),
            tuple(listOf("application/json", "application/*", "text/html", "*/*"), setOf("*/*")),
            tuple(listOf("application/json;q=0.8", "application/*;charset=utf-8"), setOf("application/*")),
        ).forEach { t ->
            val input = t.a.mapNotNull { ContentType.parse(it) }
            val expected = t.b.map { ContentType.parse(it) }.toSet()
            test("$input -> $expected") {
                val parsed = ContentType.minimize(input, ignoreParams = true)
                parsed shouldBe expected
            }
        }
    }
})
