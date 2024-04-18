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

package com.teamdev.jxbrowser.examples

import com.teamdev.jxbrowser.browser.callback.StartDownloadCallback
import com.teamdev.jxbrowser.download.event.DownloadFinished
import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.register
import com.teamdev.jxbrowser.dsl.subscribe
import com.teamdev.jxbrowser.engine.RenderingMode
import kotlin.io.path.createTempDirectory

/**
 * This example demonstrates how to allow/disallow downloading a file
 * and get a notification when downloading completes.
 */
fun main() {
    val engine = Engine(RenderingMode.HARDWARE_ACCELERATED)
    val browser = engine.newBrowser()

    browser.register(StartDownloadCallback { params, tell ->
        val download = params.download()
        val destFilePath = createTempDirectory().resolve(
            download.target().suggestedFileName()
        )

        download.subscribe<DownloadFinished> {
            println("File downloaded to `${destFilePath}`.")
            engine.close() // Exits from `main` as the download completes.
        }

        // Allows to download a file to the given file path.
        tell.download(destFilePath)
    })

    browser.navigation().loadUrl(FILE_TO_DOWNLOAD)
}

private const val FILE_TO_DOWNLOAD =
    "https://storage.googleapis.com/cloud.teamdev.com/downloads/jxbrowser/7.0/jxbrowser-7.0-cross-desktop-win_mac_linux.zip"
