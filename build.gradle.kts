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
    // .env support
    implementation("me.paulschwarz:spring-dotenv:4.0.0")
    implementation("org.duckdb:duckdb_jdbc:1.4.0")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
