# Development

This is a development focused supplement
to [CONTRIBUTING.md](https://github.com/coditory/.github/blob/main/CONTRIBUTING.md).

## Pre commit hook (optional)

Installing pre-commit hook is optional but can save you some headache when pushing unformatted code.

Installing git pre-commit hook that formats code with [Ktlint](https://pinterest.github.io/ktlint):

```sh
cp scripts/git/pre-commit .git/hooks/pre-commit
```

## Commit messages

Before writing a commit message read [this article](https://chris.beams.io/posts/git-commit/).

## Build

Before pushing any changes make sure project builds without errors with:

```sh
./gradlew build
```

## Unit tests

This project uses [Kotest](https://kotest.io/) for testing.

- Make sure tests clearly document new features
- Any new feature must be unit tested

Test coverage report is generated with:

```sh
./gradle build coverage
# See: build/reports/kover/html/index.html
```

## Validate changes locally

Before submitting a pull request test your changes locally on a sample project.
You can test locally by publishing this library to maven local repository with
`./gradlew publishToMavenLocal`.

## Validating with snapshot release (optional)

Snapshot release is triggered manually by code owners.
To use a released snapshot version make sure to register Sonatype snapshot repository in gradle with:

```
// build.gradle.kts
repositories {
    mavenCentral()
    maven {
        url = URI("https://oss.sonatype.org/content/repositories/snapshots")
    }
}
```

## Documentation

If change adds new feature or modifies an existing one update [README](/README.md) and [documentation](/docs).

To locally preview documentation changes follow instructions in [docs/README.md](/docs)
