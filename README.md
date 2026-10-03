# Online Bookstore (Spring Boot) - SonarQube training project

A small but realistic **Online Bookstore REST API** written in Spring Boot 3 / Java 17.
It is the hands-on project for the SonarQube lab: the code base contains a lot of
code-quality problems of many kinds, grouped in **five difficulty levels**. Your job is to
scan the project with SonarQube, understand what the analyser reports, fix what should be
fixed, and decide consciously what should not.

> The application builds, the tests pass and the app starts. Nothing in the source tells you
> where the problems are - let SonarQube do that.

## Stack

Spring Boot 3.3 - Spring Web - Spring Data JPA + H2 (in memory) - Spring Security -
Bean Validation - Actuator - JUnit 5 / Mockito / MockMvc - JaCoCo - Maven.

Domain: users, books, orders, payments and reviews.

## Prerequisites

* Docker (the Maven and Java toolchain run in containers - no local Java required)
  *or* a local JDK 17 + Maven 3.9
* SonarQube Community Build running at <http://localhost:9000>
* A SonarQube **user token** (My Account > Security > Generate Tokens).
  Keep it in an environment variable, never in a file:

```bash
export SONAR_TOKEN=<your token>
```

All commands below are run from this directory (`sonar-exercice-springboot`).

## Build, test, coverage

Without a local Maven, use Docker (the `~/.m2` mount caches dependencies between runs):

```bash
docker run --rm -v "$PWD":/app -v $HOME/.m2:/root/.m2 -w /app \
  maven:3.9-eclipse-temurin-17 mvn clean verify
```

With a local Maven: `mvn clean verify`.

This compiles the code, runs all tests and writes the coverage report to
`target/site/jacoco/jacoco.xml` (HTML version: `target/site/jacoco/index.html`).
Always use `clean`, otherwise old JaCoCo execution data is mixed into the report.

## Run the application

```bash
docker run --rm -p 8080:8080 -v "$PWD/target":/app -w /app \
  maven:3.9-eclipse-temurin-17 java -jar bookstore-0.1.0-SNAPSHOT.jar
```

(or `mvn spring-boot:run` locally). Then try:

```bash
curl "http://localhost:8080/api/catalog/books?size=3"
curl "http://localhost:8080/api/catalog/search?q=dune"
curl "http://localhost:8080/api/pricing/quote/1?quantity=6&tier=GOLD"
curl http://localhost:8080/actuator/health
```

Seed data (3 users, 6 books, 3 reviews) is loaded from `src/main/resources/data.sql`.
Demo accounts: `alice` / `alice123`, `bob` / `bob123`, `clerk` / `clerk123`
(fake data, in-memory database only).

## Scan with SonarQube

First build and run the tests (see above) so that `target/classes` and the JaCoCo report exist.

### Option A - SonarScanner CLI

```bash
# local install (macOS: brew install sonar-scanner)
sonar-scanner -Dsonar.token=$SONAR_TOKEN

# or via Docker (the container reaches the host through host.docker.internal)
docker run --rm -v "$PWD":/usr/src -e SONAR_TOKEN \
  sonarsource/sonar-scanner-cli -Dsonar.host.url=http://host.docker.internal:9000
```

Settings are read from `sonar-project.properties`.

### Option B - Maven plugin

```bash
mvn clean verify sonar:sonar -Dsonar.token=$SONAR_TOKEN

# via Docker
docker run --rm -v "$PWD":/app -v $HOME/.m2:/root/.m2 -w /app -e SONAR_TOKEN \
  maven:3.9-eclipse-temurin-17 \
  mvn clean verify sonar:sonar -Dsonar.host.url=http://host.docker.internal:9000 -Dsonar.token=$SONAR_TOKEN
```

Open <http://localhost:9000/dashboard?id=jadli-springboot> when the analysis has finished.

## Level -> package map

| Level | Theme | Packages (under `com.jadli.bookstore`) |
|-------|-------|--------------------------------------|
| 1 - Beginner | Everyday code smells | `level1` (catalog) |
| 2 - Easy / Intermediate | Bugs and reliability | `level2` (users, reviews, orders) |
| 3 - Intermediate | Vulnerabilities and security hotspots | `level3.config`, `level3.auth`, `level3.reports`, `level3.files`, `level3.admin`, `level3.payments` |
| 4 - Advanced | Maintainability, duplication, coverage, test quality | `level4.pricing`, `level4.orders`, `level4.billing` |
| 5 - Expert | Realistic mixed capstone: concurrency, Spring pitfalls, triage | `level5.stats`, `level5.mail`, `level5.jobs`, `level5.pricing`, `level5.context`, `level5.config`, `level5.payments`, `level5.recommend`, `level5.legacy` |

Shared code that is **not** part of the exercises: `domain` (JPA entities), `repository`
(Spring Data repositories), `BookstoreApplication`. Tests live in the matching
`src/test/java/.../levelN` packages.

## How to work

1. Scan the untouched project once and keep that result as your baseline.
2. In SonarQube use *Issues > Filters* (and the *Code* tab, which lets you browse by
   directory) to look at one level at a time.
3. For every issue: read **Why is this an issue?**, decide *true positive / false positive /
   won't fix*, and either fix it or change its status with a comment.
4. After each batch of fixes: `mvn clean verify`, re-scan, compare.
5. Do not break behaviour - the tests must stay green. Add tests when you change logic.

## Exercises

### Level 1 - Beginner (code smells)

1. Open the project overview. How many issues are there? Which issue *types* and *severities*
   do you see? Where is the technical debt estimate?
2. Filter the issues to `level1`. Group them by rule. Pick the three rules with the most
   occurrences and read their descriptions.
3. Remove everything that is unused (imports, variables, members, parameters).
4. Replace console output with a proper logger and clean up commented-out code and
   leftover task markers. Which of them should become tickets instead of disappearing?
5. Make names follow the Java conventions. Why is renaming a public method riskier than
   renaming a local variable?
6. Replace repeated string literals and obvious magic numbers with constants.
7. Look at how Spring beans receive their collaborators and at how request mappings are
   declared. Make them follow current Spring recommendations.
8. Review the test classes of this level for style problems.
9. Find an empty method or block. Decide whether to implement it, remove it, or document why
   it is intentionally empty.
10. Re-scan. Verify the *Code Smells* count for `level1` dropped and coverage did not.

### Level 2 - Easy / Intermediate (bugs and reliability)

1. Switch the issue filter to type **Bug** (or the *Reliability* quality). Which bugs are in `level2`?
2. For each null-related finding, write a small failing test or reproduce it with `curl`
   first, then fix it. Prefer a design that makes the problem impossible (for example
   returning empty collections instead of `null`).
3. Find places where values are compared the wrong way, results are ignored, or an
   exception is created but never used. Explain the runtime effect of each.
4. Find the resource that is not released on every path. Fix it with the idiomatic Java construct.
5. Study the money calculations. Which numeric types are used and what can go wrong? (Not every
   weakness is reported by the default rule set - discuss what a reviewer should add.)
6. Look at how transactions are declared on the order service. Will they work the way the
   author intended? Prove it with a test.
7. Examine the REST controllers: request parameters, path variables, HTTP status codes,
   and what is accepted as a request body. What could a client do that the author did not expect?
8. Add or improve tests until `level2` has at least 80 % line coverage, and look at the
   *branch* coverage too.

### Level 3 - Intermediate (vulnerabilities and security hotspots)

1. Open the **Security Hotspots** page and the *Vulnerability* issues. What is the
   difference between a vulnerability and a hotspot? Review each hotspot: *Safe* or
   *Fixed* (never leave it *To review* without a reason).
2. Harden the Spring Security configuration: request forgery protection, cross-origin
   policy, session handling, debug features, how passwords are stored and where credentials
   come from.
3. Replace weak cryptography and weak randomness. What would you use for password storage,
   for token generation, for encrypting card data?
4. Find every place where SQL is assembled from strings. Rewrite them with parameters.
5. Find the code that talks to external systems, reads files by name or runs operating-system
   commands based on request input. Design a safe alternative for each (allow-lists,
   canonical paths, no shell).
6. Find the cookies that are created manually. Which flags are missing?
7. Find the regular expression that can hang the server on crafted input. Rewrite it and
   prove the difference with a timing test.
8. Not every security problem is found by SonarQube Community Build (some need the
   taint-analysis of paid editions, some live in configuration files or in log statements).
   Do a manual review of `application.properties` and the logging in `level3.auth`.
   Write down what you find.
9. Level 3 deliberately has low test coverage. Add tests for the fixes you make
   (MockMvc security tests with and without credentials are a good start).

### Level 4 - Advanced (maintainability, duplication, coverage, test quality)

1. Open the *Measures* tab. Compare **Cognitive Complexity**, **Cyclomatic Complexity**,
   **Duplications** and **Coverage** per file. Which three files should a team refactor first? Why?
2. Refactor the pricing logic so that no method exceeds the default cognitive-complexity
   threshold. You may use strategy objects, lookup tables or small private methods. Pin
   the behaviour with **characterization tests first** (the current code has none).
3. Break up the class that does everything in `level4.orders`. Which responsibilities can
   you name? Which methods have too many parameters, and what object would group them?
4. Find the duplicated blocks (*Measures > Duplications*, or the *Code* tab). Extract them
   so that duplication of the new code stays below 3 %.
5. Methods that do exactly the same thing under different names: find and merge them.
6. Study the existing test class of this level. List every test smell you find and explain
   why each one gives false confidence. Fix them.
7. Raise coverage of `level4` above 80 % *with meaningful assertions* (not just calls).

### Level 5 - Expert (realistic capstone)

1. Triage: before you change anything, sort the `level5` findings into *fix now*,
   *fix later (ticket)*, *won't fix*, *false positive*. Justify each decision in a comment in
   SonarQube.
2. Concurrency: this application serves many requests at the same time. Find the classes
   that are shared between threads, and everything that is not safe in that situation.
3. Spring: several annotations are used in ways that silently do not do what the author
   expects (injection, asynchronous methods, scoping, transactions, bean naming). Find them and
   write a test that proves the intended behaviour once fixed.
4. Exceptions: find code that catches too much, throws too generic types, or interferes
   with the normal flow of `finally`.
5. Find legacy API usage. What replacement does the documentation of each recommend?
6. Find the object whose constructor calls something overridable. Write a test that shows
   the surprising result.
7. Two findings in this level look serious but may not be. Investigate them in context,
   compare them with a similar finding from level 3, and decide with evidence.
8. Reach 80 % *branch* coverage on `level5`.

### Quality Gate challenge

1. In SonarQube, check which Quality Gate the project uses (*Project Settings > Quality Gate*).
   The standard *Sonar way* gate judges **new code**: coverage on new code >= 80 %, no
   new bugs, no new vulnerabilities, security hotspots reviewed = 100 %, duplicated lines on
   new code <= 3 %, maintainability rating A.
2. Set the new-code definition to **Previous version**, change `sonar.projectVersion` in
   `sonar-project.properties` (for example to `0.2.0`) and run a scan to define the baseline.
3. Make a change (a new feature or a refactoring) in the project and scan again.
   Your task: **the Quality Gate must be green** while the change touches code of at least two levels.
   Document which conditions were red at first and what you did about each.
4. Bonus: create your own Quality Gate "JADLI Strict" (coverage on new code >= 90 %, new
   issues = 0) and assign it to the project.

### Triage exercise

Find the two findings in `level5.recommend` that use cryptography or random numbers.
Find the similar finding in `level3.auth`. Decide for each one: *True positive*, *False positive*
or *Won't fix*. Which status would you give in SonarQube and what comment would a future
maintainer need to read?

## Sonar concepts primer

* **Bug** - code that is probably wrong and will misbehave at run time (*reliability*).
* **Vulnerability** - code an attacker can exploit (*security*).
* **Security Hotspot** - security-sensitive code that needs a **human review**: it is only a
  problem in some contexts. You mark it *Safe* or *Fixed*, you do not "fix" a hotspot by default.
  (In recent SonarQube versions some former hotspots are reported as vulnerabilities directly.)
* **Code Smell** - code that works but is hard to read, change or test (*maintainability*).
  Each smell adds *technical debt* (estimated minutes to fix).
* **Severity / impact** - how serious a finding is (Blocker, High, Medium, Low, Info; older
  versions: Blocker, Critical, Major, Minor, Info).
* **Rule and Quality Profile** - an analysis checks the rules active in the project's quality
  profile for each language (default *Sonar way*). Some rules are not active by default.
* **Quality Gate** - a pass/fail decision made of conditions on metrics. The default
  gate concentrates on **new code**, which is the "clean as you code" idea: do not make it worse.
* **New code period** - what counts as new code (previous version, number of days, reference branch).
* **Coverage** - share of lines/branches executed by tests; read from the JaCoCo XML report.
* **Duplications** - copy-pasted blocks. **Cognitive complexity** - how hard a method is to
  understand (nesting counts extra).
* **Issue status** - Open, Confirmed, False positive, Accepted ("Won't fix"), Fixed. Always
  leave a comment when you close an issue without changing the code.

## Project layout

```
sonar-exercice-springboot/
  pom.xml                       Maven build (Spring Boot, JaCoCo, sonar-maven-plugin)
  sonar-project.properties      SonarScanner settings
  src/main/java/com/jadli/bookstore/
    BookstoreApplication.java
    domain/  repository/        shared JPA entities and Spring Data repositories
    level1/ ... level5/         exercise packages (see the map above)
  src/main/resources/           application.properties, data.sql (seed data)
  src/test/java/com/jadli/bookstore/level1 ... level5   tests
```
