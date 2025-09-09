import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

group = "rk.cantineocatecumenale"
version = "1.0.0"

val javaVersion = JavaVersion.VERSION_21

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.test.logger)
    alias(libs.plugins.dokka)
    alias(libs.plugins.detekt)
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

dependencies {
    detektPlugins(libs.detekt)

    implementation(libs.jackson.module)
    implementation(libs.jackson.datatype)

    implementation(libs.logback)
    implementation(libs.kotlin.logging)

    implementation(libs.kotlinx.core)
    implementation(libs.jsoup)

    testImplementation(libs.kotlinx.test)
    testImplementation(libs.mockk)
    testImplementation(kotlin("test-junit5"))
}
testlogger {
    showStackTraces = false
    showFullStackTraces = false
    showCauses = false
    slowThreshold = 10000
    showSimpleNames = true
}

tasks.test {
    jvmArgs("-XX:+EnableDynamicAgentLoading")
    useJUnitPlatform()
}

tasks.dokkaHtml {
    outputDirectory.set(layout.buildDirectory.dir("dokka")) // output directory of dokka documentation.
    // source set configuration.
    dokkaSourceSets {
        named("main") { // source set name.
            jdkVersion.set(java.targetCompatibility.toString().toInt()) // Used for linking to JDK documentation
            skipDeprecated.set(false)
            includeNonPublic.set(true) // non-public modifiers should be documented
        }
    }
}

kotlin {
    compilerOptions {
        verbose = true // enable verbose logging output
        jvmTarget.set(JvmTarget.fromTarget(java.targetCompatibility.toString()))
    }
}

detekt {
    source.setFrom("src/main/kotlin")
    config.setFrom("$projectDir/detekt.yml")
    autoCorrect = true
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = JvmTarget.JVM_21.target
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = JvmTarget.JVM_21.target
}
