plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "de.softcouture.socrates.badgeprep"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.9.2")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}
