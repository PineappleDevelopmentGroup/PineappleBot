plugins {
    kotlin("jvm") version "1.9.22"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("application")
}

group = "sh.miles"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.miles.sh/private") {
        credentials {
            this.username = System.getenv("PINEAPPLE_REPOSILITE_USERNAME")
            this.password = System.getenv("PINEAPPLE_REPOSILITE_PASSWORD")
        }
    }
}

dependencies {
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")

    compileOnly("org.jetbrains:annotations:24.0.1")

    implementation("net.dv8tion:JDA:5.0.0-beta.19")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.xerial:sqlite-jdbc:3.44.1.0")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1-Beta")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")

    implementation("sh.miles.rind.verification:decryption:1.0.0-SNAPSHOT")
}

application {
    mainClass = "sh.miles.pineapplebot.Main"
}

tasks.shadowJar {
    archiveFileName = "PineappleBot-${rootProject.version}.jar"
    archiveClassifier = ""
}

tasks.build {
    dependsOn(tasks.shadowJar)
}