import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.PosixFilePermission.*
import java.util.*
import java.util.zip.ZipFile

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

dependencies {
    implementation("org.seleniumhq.selenium:selenium-chrome-driver:4.26.0")
}

// The Chromium version used in JxBrowser.
val chromiumVersion = "130.0.6723.70"

fun chromeDriverPlatform(): String {
    fun isArm(): Boolean {
        val arch = System.getProperty("os.arch")
        return "aarch64" == arch || "arm" == arch
    }

    val os = System.getProperty("os.name")
    return when {
        os.startsWith("Windows") -> {
            "win64"
        }

        os.startsWith("Linux") -> {
            "linux64"
        }

        os.startsWith("Mac") -> {
            if (isArm()) {
                "mac-arm64"
            } else {
                "mac-x64"
            }
        }

        else -> {
            throw IllegalStateException("Unsupported operating system.")
        }
    }
}

tasks.register("downloadChromeDriver") {
    val chromeDriverPlatform = chromeDriverPlatform()
    val downloadUrl =
        "https://storage.googleapis.com/chrome-for-testing-public/$chromiumVersion/$chromeDriverPlatform/chromedriver-$chromeDriverPlatform.zip"
    val resourcesDir = sourceSets["main"].resources.srcDirs.first()
    if (!resourcesDir.exists()) {
        mkdir(resourcesDir)
    }
    val chromeDriverZip = resourcesDir.resolve("chromedriver.zip")
    val chromeDriver = resourcesDir.resolve("chromedriver")

    doLast {
        URL(downloadUrl).openStream().use { inputStream ->
            Files.copy(
                inputStream,
                chromeDriverZip.toPath(),
                StandardCopyOption.REPLACE_EXISTING
            )
            println("ChromeDriver downloaded to ${chromeDriverZip.absolutePath}")
            ZipFile(chromeDriverZip).use { zip ->
                zip.entries().asSequence().forEach { entry ->
                    if (!entry.isDirectory) {
                        val outputFile =
                            File(resourcesDir, File(entry.name).name)
                        zip.getInputStream(entry).use { input ->
                            outputFile.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                        val extension = outputFile.extension
                        if (extension.isEmpty() &&
                            !System.getProperty("os.name").startsWith("Windows")
                        ) {
                            Files.setPosixFilePermissions(
                                outputFile.toPath(),
                                EnumSet.of(
                                    OWNER_EXECUTE,
                                    OWNER_READ,
                                    OWNER_WRITE,
                                    GROUP_EXECUTE,
                                    GROUP_READ,
                                    GROUP_WRITE
                                )
                            )
                        }
                    }
                }
                println("ChromeDriver extracted to ${chromeDriver.absolutePath}")
            }
            delete(chromeDriverZip)
        }
    }
}
