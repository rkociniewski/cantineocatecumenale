import org.jetbrains.kotlin.gradle.dsl.JvmTarget

group = "rk.cantineocatecumenale"
version = "1.0.0"

val javaVersion = JavaVersion.VERSION_21

val jacksonVersion: String by project
val jsoupVersion: String by project
val kotlinLoggingVersion: String by project
val kotlinxVersion: String by project
val logbackVersion: String by project
val mockkVersion: String by project

plugins {
    kotlin("jvm")
    id("com.adarshr.test-logger")
    id("org.jetbrains.dokka")
    id("org.graalvm.buildtools.native")
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.github.oshai:kotlin-logging-jvm:$kotlinLoggingVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxVersion")
    implementation("org.jsoup:jsoup:$jsoupVersion")

    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation(kotlin("test-junit5"))
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

application {
    mainClass.set("rk.cantineocatecumenale.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

graalvmNative {
    binaries {
        named("main") {
            javaLauncher.set(javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(javaVersion.toString()))
                vendor.set(JvmVendorSpec.GRAAL_VM)
            })

            buildArgs.addAll(
                listOf("--enable-url-protocols=http,https")
            )
        }
    }
}

tasks.dokkaHtml {
    outputDirectory.set(layout.buildDirectory.dir("dokka"))
    dokkaSourceSets {
        named("main") {
            jdkVersion.set(21)
            skipDeprecated.set(false)
            includeNonPublic.set(true)
        }
    }
}

kotlin {
    compilerOptions {
        verbose = true
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

testlogger {
    showStackTraces = false
    slowThreshold = 10000
    showSimpleNames = true
}
