plugins {
    `java-library`
    `maven-publish`
}

group = "com.asyncsite.locks"
version = "0.1.0"

java {
    toolchain { languageVersion = JavaLanguageVersion.of(21) }
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencies {
    api("org.springframework.boot:spring-boot-autoconfigure:3.5.3")
    api("org.springframework.boot:spring-boot-starter-aop:3.5.3")
    compileOnly("org.springframework.data:spring-data-redis:3.3.1")
    implementation("org.slf4j:slf4j-api:2.0.16")
}

publishing {
    publications {
        create<MavenPublication>("gpr") {
            from(components["java"])
            groupId = project.group as String
            artifactId = "asyncsite-distributed-lock"
            version = project.version as String
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/AsyncSite/asyncsite-distributed-lock")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
