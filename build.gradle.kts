plugins {
    id("java")
    id("application")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "ru.multivarka"
version = "1.0.0"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(8)
}

application {
    mainClass.set("ru.multivarka.Main")
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
}

tasks.jar {
    enabled = false
    archiveClassifier.set("plain")
    manifest {
        attributes["Main-Class"] = "ru.multivarka.Main"
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
    manifest {
        attributes["Main-Class"] = "ru.multivarka.Main"
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.distZip {
    enabled = false
}

tasks.distTar {
    enabled = false
}

tasks.startScripts {
    enabled = false
}

tasks.startShadowScripts {
    enabled = false
}

tasks.shadowDistZip {
    enabled = false
}

tasks.shadowDistTar {
    enabled = false
}
