/*
 *  Copyright 2025, TeamDev. All rights reserved.
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

repositories {
    mavenCentral()
    maven("https://europe-maven.pkg.dev/jxbrowser/releases")
}

configurations {
    create("jxbrowserPlatform")
}

val jxBrowserVersion: String by rootProject.extra

dependencies {
    // Core JxBrowser library.
    dependencies.add("jxbrowserPlatform", "com.teamdev.jxbrowser:jxbrowser:$jxBrowserVersion")

    // Uncomment when SWT artifact is available.
    // SWT dependency
    // -------------------------
    dependencies.add("jxbrowserPlatform", "com.teamdev.jxbrowser:jxbrowser-swt:$jxBrowserVersion")

    // Windows-only dependency
    // -------------------------
    dependencies.add("jxbrowserPlatform", "com.teamdev.jxbrowser:jxbrowser-win32:$jxBrowserVersion")
    dependencies.add("jxbrowserPlatform", "com.teamdev.jxbrowser:jxbrowser-win64:$jxBrowserVersion")

    // macOS-only dependency
    // -------------------------
    dependencies.add("jxbrowserPlatform", "com.teamdev.jxbrowser:jxbrowser-mac:$jxBrowserVersion")

    // Linux-only dependency
    // -------------------------
    dependencies.add("jxbrowserPlatform", "com.teamdev.jxbrowser:jxbrowser-linux64:$jxBrowserVersion")

    implementation(files("$rootDir/network/src/main/resources/resource.jar"))
}

/**
 * Copies JxBrowser JAR files from the local Maven repository to the temp directory.
 */
val copyJarsFromRepository by tasks.register("copyJarsFromRepository", Copy::class) {
    from(configurations.getByName("jxbrowserPlatform"))
    into("$buildDir/temp")
}

/**
 * Copies JxBrowser JAR files to the specific folders in the workspace where they belong.
 *
 * For example, `jxbrowser-win32-7.5.jar` is copied to `jxbrowser-win32` folder.
 */
val copyJarsToPlugIns by tasks.register("copyJarsToPlugIns") {
    dependsOn(copyJarsFromRepository)
    doLast {
        listOf("win32", "win64", "linux64", "mac").forEach { platform ->
            copy {
                from(file("$buildDir/temp/jxbrowser-$platform-$jxBrowserVersion.jar"))
                into(file("$projectDir/jxbrowser-$platform"))
                rename { _ -> "jxbrowser-$platform.jar" }
            }
            mkdir("$projectDir/jxbrowser-$platform/src")
        }

        mkdir("$projectDir/jxbrowser/src")

        copy {
            from(file("${buildDir}/temp/jxbrowser-$jxBrowserVersion.jar"))
            into(file("${projectDir}/jxbrowser"))
            rename { _ -> "jxbrowser.jar" }
        }

        copy {
            from(file("${buildDir}/temp/jxbrowser-swt-$jxBrowserVersion.jar"))
            into(file("${projectDir}/jxbrowser"))
            rename { _ -> "jxbrowser-swt.jar" }
        }
    }
}

/**
 * Removes JAR files copied to the Eclipse workspace.
 */
val cleanUp by tasks.register("cleanUp", Delete::class) {
    delete(fileTree(projectDir) {
        include("**/*.jar")
    })
}

tasks.clean {
    dependsOn(cleanUp)
}

val prepareEclipseExample by tasks.register("prepareEclipseExample") {
    group = "Eclipse RCP Example"
    description = "Downloads dependencies for the project and puts them to the right places"
    dependsOn(copyJarsToPlugIns)
}
