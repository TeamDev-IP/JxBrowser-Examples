/*
 *  Copyright 2026, TeamDev. All rights reserved.
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

import org.gradle.api.JavaVersion
import org.gradle.api.Project

/**
 * A dependency on SWT.
 */
object Swt {

    private const val groupdId = "org.eclipse.platform"
    private val version = if (JavaVersion.current().isJava8) {
        // This version corresponds to Eclipse 4.8.0.
        "3.107.0"
    } else {
        // This version also supports Apple Silicon.
        "3.126.0" // Eclipse 4.28.0
    }
    private val platformDependency = "$groupdId:org.eclipse.swt.${osgiPlatform()}:$version"

    /**
     * The dependency to SWT.
     */
    public val toolkitDependency = "$groupdId:org.eclipse.swt:$version"

    fun configurePlatformDependency(project: Project) {
        project.configurations.all {
            resolutionStrategy {
                dependencySubstitution {
                    substitute(module("org.eclipse.platform:org.eclipse.swt.\${osgi.platform}"))
                            .because("The Maven property osgi.platform is not handled by Gradle")
                            .using(module(platformDependency))
                }
            }
        }
    }

    /**
     * Returns the platform bit of the SWT artifact names.
     */
    private fun osgiPlatform(): String {
        val os = System.getProperty("os.name").lowercase()
        val arch = System.getProperty("os.arch")
        val isArm = "aarch64" == arch || "arm" == arch
        return when {
            os.contains("win") ->  {
                if (isArm) {
                    "win32.win32.aarch64"
                } else {
                    "win32.win32.x86_64"
                }
            }
            os.contains("linux") -> {
                if (isArm) {
                    "gtk.linux.aarch64"
                } else {
                    "gtk.linux.x86_64"
                }
            }

            os.contains("mac") -> {
                if (isArm) {
                    "cocoa.macosx.aarch64"
                } else {
                    "cocoa.macosx.x86_64"
                }
            }

            else -> {
                throw IllegalStateException("Unexpected operating system")
            }
        }
    }
}
