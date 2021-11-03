# thumbnails4j Contributor's Guide

Thank you for your interest in contributing to thumbnails4j!

How to build and contribute to thumbnails4j.

### Requirements

- Java 8
- OS: Unix/Linux

### Installing dependencies

From the root level of this repository:

```shell
./mvnw clean install -DskipTests
```

### Building

For all projects, run from project root. For single module, run from
module root.

```shell
# Build
./mvnw clean install -DskipTests
```

### Testing

For all projects, run from project root. For single module, run from
module root.

```shell
./mvnw clean test
```

It is expected that any contribution will include unit tests. Tests must be passing in order to merge any pull request.

### Branching Strategy

Our `main` branch holds the latest development code for the next release. If the next release will be a minor release, 
the expecation is that no breaking changes will be in `main`. If a change would be breaking, we need to put it behind a
feature flag, or make it an opt-in change. We will only merge breaking PRs when we are ready to start working on the 
next major.

All PRs should be created from a fork, to keep a clean set of branches on `origin`.

Releases should be performed directly in `main` (or a minor branch for patches), following the Publishing guide below.

We will create branches for all minor releases.

### Publishing

Publish a new major or minor from `main`
(Example, publishing 1.1.0)

TODO. Currently working through the release mechanics in https://github.com/elastic/infra/issues/32555

Publish a patch
(Example, publish 0.6.1)

TODO. Currently working through the release mechanics in https://github.com/elastic/infra/issues/32555


### Known usages
* Elastic Enterprise Search uses thumbnails4j to generate thumbnail images for its Workplace Search product.

Are you using thumbnails4j? If so, add your usecase here!