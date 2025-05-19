# kttp - Kotlin HTTP

[![Build](https://github.com/coditory/ktserver/actions/workflows/build.yml/badge.svg)](https://github.com/coditory/ktserver/actions/workflows/build.yml)
[![Coverage](https://codecov.io/gh/coditory/ktserver/graph/badge.svg?token=FlAX0WyFod)](https://codecov.io/gh/coditory/ktserver)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.coditory.ktserver/ktserver/badge.svg)](https://mvnrepository.com/artifact/com.coditory.ktserver/ktserver)

**🚧 This library as under heavy development until release of version `1.x.x` 🚧**

> Thin kotlin HTTP layer with multiple server/client implementations.
> Provides coroutine friendly API without NO extension functions.

## Sample usage

Add dependency to `build.gradle.kts`:

Thin wrapper around [`com.sun.net.httpserver.HttpServer`](https://docs.oracle.com/en/java/javase/22/docs/api/jdk.httpserver/com/sun/net/httpserver/HttpServer.html).
- Coroutine friendly API for HttpServer
- Routing, filters, handlers
- Minimal dependencies - perfect to use in tests using http server mock

```kts
dependencies {
  implementation("com.coditory.kttp:kttp-server-jdk:0.0.1")
}
```

## TBD

- Handle head
- Handle 405 response - Method not allowed
- Handle 406 response - Not Acceptable
- Upload file
- Serve files
- Websocket
- kttp client
- more tests
- add netty implementation for production usage
