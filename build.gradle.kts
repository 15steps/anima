plugins {
    kotlin("jvm") version "1.3.61"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    // eval math exp
    implementation("com.udojava:EvalEx:2.1")
    implementation("log4j:log4j:1.2.16")
    implementation("com.squareup.okhttp3:okhttp:4.3.1")
    implementation("com.squareup.okio:okio:2.4.3")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.2")

    testImplementation("junit:junit:4.13")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}