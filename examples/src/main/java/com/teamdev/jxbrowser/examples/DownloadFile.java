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

package com.teamdev.jxbrowser.examples;

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;

import com.teamdev.jxbrowser.browser.callback.StartDownloadCallback;
import com.teamdev.jxbrowser.download.event.DownloadFinished;
import com.teamdev.jxbrowser.engine.Engine;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This example demonstrates how to allow/disallow downloading a file and get
 * notification when downloading has been completed.
 */
public final class DownloadFile {

    private static final String URL_TO_DOWNLOAD =
            "https://storage.googleapis.com/cloud.teamdev.com/downloads/jxbrowser/7.0/jxbrowser-7.0-cross-desktop-win_mac_linux.zip";

    public static void main(String[] args) {
        var engine = Engine.newInstance(HARDWARE_ACCELERATED);
        var browser = engine.newBrowser();

        browser.set(StartDownloadCallback.class, (params, tell) -> {
            var download = params.download();
            var destFilePath = createTempDirectory().toAbsolutePath()
                    .resolve(download.target().suggestedFileName());

            download.on(DownloadFinished.class, event -> {
                System.out.println("File downloaded to " + destFilePath);
                engine.close(); // Exits from `main` as the download completes.
            });

            // Allows to download a file to the given file path.
            tell.download(destFilePath);
        });

        browser.navigation().loadUrl(URL_TO_DOWNLOAD);
    }

    private static Path createTempDirectory() {
        try {
            return Files.createTempDirectory("Downloads");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create a temp dir", e);
        }
    }
}
