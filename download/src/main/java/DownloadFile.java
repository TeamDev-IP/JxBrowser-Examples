/*
 *  Copyright 2020, TeamDev. All rights reserved.
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

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.download.Download;
import com.teamdev.jxbrowser.download.Downloads;
import com.teamdev.jxbrowser.download.callback.StartDownloadCallback;
import com.teamdev.jxbrowser.download.event.DownloadFinished;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.internal.FileUtil;
import com.teamdev.jxbrowser.view.swing.BrowserView;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;

/**
 * This example demonstrates how to allow/disallow downloading a file
 * and get notification when downloading has been completed.
 */
public final class DownloadFile {

    private static final String urlToDownload =
            "https://storage.googleapis.com/cloud.teamdev.com/downloads/jxbrowser/7.0/jxbrowser-7.0-cross-desktop-win_mac_linux.zip";

    public static void main(String[] args) {
        Engine engine = Engine.newInstance(
                EngineOptions.newBuilder(HARDWARE_ACCELERATED).build());
        Browser browser = engine.newBrowser();

        SwingUtilities.invokeLater(() -> {
            BrowserView view = BrowserView.newInstance(browser);

            JFrame frame = new JFrame("Download File");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(view, BorderLayout.CENTER);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        engine.downloads().set(StartDownloadCallback.class, (params, tell) -> {
            params.download().on(DownloadFinished.class, event ->
                    System.out.println("File downloaded!"));
            tell.download(createTempDirectory().toAbsolutePath());
        });

        browser.navigation().loadUrl(urlToDownload);
    }

    private static Path createTempDirectory() {
        try {
            return Files.createTempDirectory("Downloads");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create a temp dir", e);
        }
    }
}
