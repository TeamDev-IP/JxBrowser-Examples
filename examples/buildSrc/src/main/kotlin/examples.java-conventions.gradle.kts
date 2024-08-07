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
    // Adds JxBrowser.
    id("com.teamdev.jxbrowser")
}

val jxBrowserVersion: String by rootProject.extra

group = "com.teamdev.jxbrowser-examples"
version = jxBrowserVersion

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
}

tasks.withType<JavaExec> {
    // Assign all Java system properties from
    // the command line to the JavaExec task.
    systemProperties(System.getProperties().mapKeys { it.key as String })
}
