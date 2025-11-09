plugins {
	java
	id("org.springframework.boot") version "3.5.7"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "org.inn"
version = "0.0.1"
description = "MailSense — The Inbox That Understands."

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.projectlombok:lombok")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // ... your existing dependencies
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Liquibase runtime (Spring Boot will run it on startup)
    implementation("org.liquibase:liquibase-core:4.23.2")
    // Postgres JDBC
    runtimeOnly("org.postgresql:postgresql:42.6.0")

    testImplementation("com.h2database:h2:2.2.222")

    // OAuth2
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // Optional: WebClient (for custom token exchange)
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // JWT (for your app's own tokens)
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // .env support
    implementation("me.paulschwarz:spring-dotenv:4.0.0")

}

tasks.withType<Test> {
	useJUnitPlatform()
}
