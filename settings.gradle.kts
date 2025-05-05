pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenLocal()
        mavenCentral()
    }

    val dokkaId: String by settings
    val dokkaVersion: String by settings
    val kotlinId: String by settings
    val kotlinVersion: String by settings
    val testLoggerId: String by settings
    val testLoggerVersion: String by settings
    val graalVMId: String by settings
    val graalVMVersion: String by settings

    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace?.startsWith(kotlinId) == true) {
                useVersion(kotlinVersion)
            }

            when (requested.id.id) {
                testLoggerId -> useVersion(testLoggerVersion)
                dokkaId -> useVersion(dokkaVersion)
                kotlinId -> useVersion(kotlinVersion)
                graalVMId -> useVersion(graalVMVersion)
            }
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "cantineocatecumenale"