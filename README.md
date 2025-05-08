# ktserver - kotlin http server

[![Build](https://github.com/coditory/ktserver/actions/workflows/build.yml/badge.svg)](https://github.com/coditory/ktserver/actions/workflows/build.yml)
[![Coverage](https://codecov.io/gh/coditory/ktserver/graph/badge.svg?token=FlAX0WyFod)](https://codecov.io/gh/coditory/ktserver)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.coditory.ktserver/ktserver/badge.svg)](https://mvnrepository.com/artifact/com.coditory.ktserver/ktserver)

**🚧 This library as under heavy development until release of version `1.x.x` 🚧**

> Simple kotlin http server

## Sample usage

Add dependency to `build.gradle.kts`:

```kts
dependencies {
  implementation("com.coditory.kttp:kttp-server-jdk:$version")
}
```

## TBD

- Rename to kttp
- Check nested routing
- Check 404 and error handling
- Handle head
- Handle 405 response - Method not allowed
- Handle 406 response - Not Acceptable
- Upload file
- Serve files
- Websocket
- klog api
- kttp client
