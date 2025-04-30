package com.coditory.kttp.headers

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.tuple
import io.kotest.matchers.shouldBe

class MediaTypeSpec : FunSpec({
    context("should parse MediaType") {
        listOf(
            tuple("", null),
            tuple(" \t\n\r; ,,\n,; = ;;, ", null),
            tuple(" application / json ", MediaType.Application.Json),
            tuple("text/html; charset=utf-8", MediaType.Text.Html.withParameter("charset", "utf-8")),
            tuple("text/vnd.acme.item+json; q=0.3;version=1.0;charset=utf-8", MediaType.Text.subtype("vnd.acme.item+json").withParameter("charset", "utf-8").withParameter("version", "1.0").withParameter("q", "0.3")),
        ).forEach { t ->
            val input = t.a
            val expected = t.b
            test("$input -> $expected") {
                val value = MediaType.parse(input)
                value shouldBe expected
            }
        }
    }

    context("should serialize MediaType") {
        listOf(
            tuple("application/json", "application/json"),
            tuple("text/html; charset=utf-8", "text/html;charset=utf-8"),
            tuple("text/vnd.acme.item+json; q=0.3;version=1.0;charset=utf-8", "text/vnd.acme.item+json;q=0.3;version=1.0;charset=utf-8"),
        ).forEach { t ->
            val input = t.a
            val expected = t.b
            test("$input -> $expected") {
                val parsed = MediaType.parse(input)!!
                parsed.toHttpString() shouldBe expected
            }
        }
    }

    context("should contain MediaType") {
        listOf(
            tuple("*/*", "*/*"),
            tuple("*/*", "application/json"),
            tuple("*/*", "application/json; charset=utf-8"),
            tuple("*/*", "application/vnd.acme.api+json"),
            tuple("*/*;charset=utf-8", "application/json;charset=utf-8"),
            tuple("application/*", "application/*"),
            tuple("application/*", "application/json"),
            tuple("application/*", "application/json;charset=utf-8"),
            tuple("application/*", "application/vnd.acme.api+json"),
            tuple("application/*;charset=utf-8", "application/json;charset=utf-8"),
            tuple("application/json", "application/json"),
            tuple("application/json", "application/json;charset=utf-8"),
            tuple("application/json", "application/vnd.acme.api+json"),
            tuple("application/json;charset=utf-8", "application/json;charset=utf-8"),
            tuple("application/vnd.acme.api+json", "application/vnd.acme.api+json"),
            tuple("application/vnd.acme.api+json", "application/vnd.acme.api+json;charset=utf-8"),
        ).forEach { t ->
            val parent = MediaType.parse(t.a)!!
            val child = MediaType.parse(t.b)!!
            test("$parent contains $child") {
                parent.contains(child) shouldBe true
            }
        }
    }

    context("should not contain MediaType") {
        listOf(
            tuple("*/*;charset=utf-8", "application/json"),
            tuple("application/*", "*/*"),
            tuple("application/*", "text/html"),
            tuple("application/*", "text/*"),
            tuple("application/*;charset=utf-8", "application/json"),
            tuple("application/json", "*/*"),
            tuple("application/json", "text/html"),
            tuple("application/json", "application/*"),
            tuple("application/json;charset=utf-8", "application/json"),
            tuple("application/vnd.acme.api+json", "application/json"),
        ).forEach { t ->
            val parent = MediaType.parse(t.a)!!
            val child = MediaType.parse(t.b)!!
            test("$parent contains $child") {
                parent.contains(child) shouldBe false
            }
        }
    }

    context("should minimize MediaType") {
        listOf(
            tuple(listOf("application/json", "application/*", "text/html", "*/html"), setOf("application/*", "*/html")),
            tuple(listOf("application/json", "application/*", "text/html", "*/*"), setOf("*/*")),
            tuple(listOf("application/json;q=0.8", "application/*;charset=utf-8"), setOf("application/json;q=0.8", "application/*;charset=utf-8")),
            tuple(listOf("application/json;charset=utf-8", "application/*;charset=utf-8"), setOf("application/*;charset=utf-8")),
        ).forEach { t ->
            val input = t.a.mapNotNull { MediaType.parse(it) }
            val expected = t.b.map { MediaType.parse(it) }.toSet()
            test("$input -> $expected") {
                val parsed = MediaType.minimize(input)
                parsed shouldBe expected
            }
        }
    }

    context("should minimize MediaType ignoring parameters") {
        listOf(
            tuple(listOf("application/json", "application/*", "text/html", "*/html"), setOf("application/*", "*/html")),
            tuple(listOf("application/json", "application/*", "text/html", "*/*"), setOf("*/*")),
            tuple(listOf("application/json;q=0.8", "application/*;charset=utf-8"), setOf("application/*")),
        ).forEach { t ->
            val input = t.a.mapNotNull { MediaType.parse(it) }
            val expected = t.b.map { MediaType.parse(it) }.toSet()
            test("$input -> $expected") {
                val parsed = MediaType.minimize(input, ignoreParams = true)
                parsed shouldBe expected
            }
        }
    }

    context("should match MediaType with file ext") {
        listOf(
            tuple("image/png", listOf("png")),
            tuple("application/json", listOf("json")),
            tuple("application/vnd.ms-excel", listOf("xls", "xlm", "xla", "xlc", "xlt", "xlw")),
        ).forEach { t ->
            val exts = t.b
            val firstExt = exts.first()
            val mediaTypeValue = t.a
            val mediaType = MediaType.parse(mediaTypeValue)!!
            test("$mediaType -> $exts") {
                MediaType.getAllFileExtByMediaType(mediaType) shouldBe exts
                MediaType.getAllFileExtByMediaTypeValue(mediaTypeValue) shouldBe exts
                MediaType.getExtByMediaTypeValue(mediaTypeValue) shouldBe firstExt
                MediaType.getExtByMediaType(mediaType) shouldBe firstExt
                mediaType.fileExt() shouldBe firstExt
                exts.forEach { ext ->
                    MediaType.getMediaTypeByExt(ext) shouldBe mediaType
                    MediaType.getMediaTypeValueByExt(ext) shouldBe mediaTypeValue
                }
            }
        }
    }
})
