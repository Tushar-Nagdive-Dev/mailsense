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
    //Spring
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Flyway Migration
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-duckdb:10.24.0") // DuckDB plugin for Flyway

    // devtools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // .env support
    implementation("me.paulschwarz:spring-dotenv:4.0.0")

    //DuckDB
    implementation("org.duckdb:duckdb_jdbc:0.2.1")

    // Logging
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
