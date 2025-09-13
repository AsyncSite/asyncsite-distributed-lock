# AsyncSite Distributed Lock (Spring Boot Starter)

Redis-based distributed lock for Spring Boot 3.x applications with simple `@DistributedLock` annotation and auto-configuration.

## Features
- Annotation-based locking: `@DistributedLock(name, key, …)`
- Redis `SET NX PX` token lock with safe unlock
- Spring AOP-based aspect, opt-in/opt-out via properties
- Minimal dependencies; works alongside existing Redis config

## Installation

### Repository (GitHub Packages)
Make sure your build can access GitHub Packages (set env or gradle.properties):

- `GITHUB_ACTOR` = your GitHub username
- `GITHUB_TOKEN` = a token with `read:packages`

#### Gradle (Kotlin DSL)
```kotlin
repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/AsyncSite/asyncsite-distributed-lock")
        credentials {
            username = findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
            password = findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
        content { includeGroup("com.asyncsite.locks") }
    }
}

dependencies {
    implementation("com.asyncsite.locks:asyncsite-distributed-lock:0.1.0")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
}
```

#### Maven
```xml
<repositories>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/AsyncSite/asyncsite-distributed-lock</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.asyncsite.locks</groupId>
    <artifactId>asyncsite-distributed-lock</artifactId>
    <version>0.1.0</version>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
  </dependency>
</dependencies>
```

## Configuration
Auto-config is enabled by default. Override via properties:

```yaml
asyncsite:
  lock:
    enabled: true          # default true
    prefix: "asyncsite:lock"
    default-wait-ms: 3000
    default-lease-ms: 3000
    backoff-ms: 50
```

- Disable for services that don't need locks (e.g., Eureka):
```yaml
asyncsite.lock.enabled: false
```

## Usage
Annotate methods that must run under a distributed lock:

```java
import com.asyncsite.locks.annotation.DistributedLock;

@DistributedLock(
    name = "checkout:create-intent",
    key = "#command.domain + ':' + #command.domainId + ':' + #command.orderId",
    waitMs = 3000,
    leaseMs = 3000,
    failure = DistributedLock.FailurePolicy.FAIL
)
public PaymentIntent createIntent(CreatePaymentIntentCommand command) {
    // business logic
}
```

- `name`: logical lock name
- `key`: SpEL to build a unique key (combined with prefix)
- `waitMs`: max wait time to acquire
- `leaseMs`: TTL for lock
- `failure`: on failure to acquire (`FAIL`, `SKIP`, or `EXECUTE`)

## Requirements & Compatibility
- Java 21, Spring Boot 3.5.x
- A Redis connection and `spring-boot-starter-data-redis`
- `spring-boot-starter-aop` for aspects

## Troubleshooting
- `NoClassDefFoundError: StringRedisTemplate`: add `spring-boot-starter-data-redis` (or disable with `asyncsite.lock.enabled=false`).
- Aspect not firing: ensure `spring-boot-starter-aop` is on the classpath.
- Lock not acquired: check Redis connectivity and key prefix collisions.

## License
Apache-2.0
