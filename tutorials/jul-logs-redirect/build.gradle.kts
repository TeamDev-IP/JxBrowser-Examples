/*
 *  Copyright 2022, TeamDev. All rights reserved.
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
    // Provides convenience methods for adding JxBrowser dependencies into a project.
    id("com.teamdev.jxbrowser.gradle") version "0.0.2"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

jxbrowser {
    // The JxBrowser version.
    version = "7.23"
}

dependencies {
    // Adds a dependency to the core JxBrowser API.
    implementation(jxbrowser.core())

    // Adds dependency to the UI toolkit integration (Swing in this example).
    implementation(jxbrowser.swing())

    // Detects the current platform and adds the corresponding Chromium binaries.
    implementation(jxbrowser.currentPlatform())

    // Adds a dependency to the SLF4J API.
    implementation("org.slf4j:slf4j-api:1.7.36")

    // Adds a dependency to the Log4j binding for SLF4J.
    implementation("org.slf4j:slf4j-log4j12:1.7.36")

    // Includes a j.u.l handler, namely SLF4JBridgeHandler, which routes all incoming jul records to the SLF4j API.
    implementation("org.slf4j:jul-to-slf4j:1.7.36")
}
