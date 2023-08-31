/*
 *  Copyright 2023, TeamDev. All rights reserved.
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

rootProject.name = "JxBrowser-Examples"

/**
 * Includes a module and sets custom project directory to it.
 */
val module: (String, String) -> Unit = { name, path ->
    include(name)
    project(":$name").projectDir = File(path)
}

include("examples")

// DOM changes
module("content-changes", "./tutorials/content-changes")
module("docker", "./tutorials/docker")
module("eclipse-rcp", "./tutorials/eclipse-rcp")
// Selenium integration
module("launcher", "./tutorials/selenium/launcher")
module("target-app", "./tutorials/selenium/target-app")
// Java web crawler
module("web-crawler", "./tutorials/web-crawler")
// Java media player
module("media-player", "./tutorials/media-player")
// JxBrowser logs redirection
module("jul-logs-redirect", "./tutorials/jul-logs-redirect")
module("serve-from-directory", "./tutorials/serve-from-directory")
module("js-java-bridge", "./tutorials/js-java-bridge")
