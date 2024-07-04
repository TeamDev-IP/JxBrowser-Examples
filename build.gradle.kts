/*
 *  Copyright 2024, TeamDev. All rights reserved.
 *
 *  Redistribution and use in source and/or binary forms, with or without
 *  modification, must retain the above copyright notice and the following
 *  disclaimer.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 *  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

plugins {
    java
    kotlin("jvm") version "2.0.0"

    // Adds JxBrowser.
    id("com.teamdev.jxbrowser") version "1.1.0"

    // Adds UI toolkits.
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.jetbrains.compose") version "1.6.11"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
}

val jxBrowserVersion by extra { "8.0.0-eap.4" } // The version of JxBrowser used in the examples.
val guavaVersion by extra { "29.0-jre" } // Some of the examples use Guava.

allprojects {
    group = "com.teamdev.jxbrowser-examples"
    version = jxBrowserVersion
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "com.teamdev.jxbrowser")
    apply(plugin = "org.openjfx.javafxplugin")
    apply(plugin = "org.jetbrains.compose")
    apply(plugin = "org.jetbrains.kotlin.plugin.compose")

    repositories {
        mavenCentral()
        google()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    jxbrowser {
        version = jxBrowserVersion
        includePreviewBuilds = true // Until JxBrowser 8 is released.
    }

    javafx {
        version = "17"
        modules = listOf("javafx.controls", "javafx.swing", "javafx.fxml")
    }

    dependencies {
        // Cross-platform dependency
        implementation(jxbrowser.crossPlatform)

        /*
           For having only platform-dependent dependency:
           1. Comment out the cross-platform dependency above.
           2. Uncomment the dependency for your platform.
        */

        // Windows 32-bit
        // implementation(jxbrowser.win32)

        // Windows 64-bit
        // implementation(jxbrowser.win64)

        // macOS 64-bit
        // implementation(jxbrowser.mac)

        // macOS 64-bit ARM
        // implementation(jxbrowser.macArm)

        // Linux 64-bit
        // implementation(jxbrowser.linux64)

        // Linux 64-bit ARM
        // implementation(jxbrowser.linuxArm)

        // JxBrowser for JavaFX dependency.
        implementation(jxbrowser.javafx)

        // JxBrowser for Swing dependency.
        implementation(jxbrowser.swing)

        // JxBrowser for Compose dependency.
        implementation(jxbrowser.compose)

        // Dependency on Compose for the current platform.
        implementation(compose.desktop.currentOs)

        // JxBrowser for SWT dependency.
        implementation(jxbrowser.swt)

        // Dependency on an SWT for the current platform.
        implementation(Swt.toolkitDependency)

        // Depend on Guava for the Resources utility class used for loading resource files into strings.
        implementation("com.google.guava:guava:$guavaVersion")

        // This file is used by `JarProtocol` example.
        runtimeOnly(files(rootDir.resolve("examples/src/main/resources/tiny-website.jar")))
    }

    tasks.withType<JavaExec> {
        // Assign all Java system properties from
        // the command line to the JavaExec task.
        systemProperties(System.getProperties().mapKeys { it.key as String })
    }

    Swt.configurePlatformDependency(project)
}
