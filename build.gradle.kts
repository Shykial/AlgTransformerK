import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("groovy")
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.spring") version "1.5.31"
    kotlin("plugin.jpa") version "1.5.31"
}

group = "com.shykial"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_16

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.5.4")
    implementation("org.springframework.boot:spring-boot-starter-web:2.5.4")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.5")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.h2database:h2:1.4.200")
    implementation("io.springfox:springfox-boot-starter:3.0.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test:2.5.5")
    testImplementation("org.codehaus.groovy:groovy-all:3.0.8")
    testImplementation("org.spockframework:spock-core:2.0-groovy-3.0")
    testImplementation("org.spockframework:spock-spring:2.0-groovy-3.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "16"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
