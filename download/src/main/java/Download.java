/*
 *  Copyright 2018, TeamDev. All rights reserved.
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

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.DownloadHandler;
import com.teamdev.jxbrowser.chromium.DownloadItem;
import com.teamdev.jxbrowser.chromium.events.DownloadEvent;
import com.teamdev.jxbrowser.chromium.events.DownloadListener;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;

/**
 * The example demonstrates how to handle file download. To cancel download
 * you must return {@code false} from the
 * {@link DownloadHandler#allowDownload(com.teamdev.jxbrowser.chromium.DownloadItem)}
 * method. To listed for download update events you can register your own
 * {@link com.teamdev.jxbrowser.chromium.events.DownloadListener}.
 */
public class Download {
    public static void main(String[] args) {
        Browser browser = new Browser();
        BrowserView view = new BrowserView(browser);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(view, BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        browser.setDownloadHandler(new DownloadHandler() {
            public boolean allowDownload(DownloadItem download) {
                download.addDownloadListener(new DownloadListener() {
                    public void onDownloadUpdated(DownloadEvent event) {
                        DownloadItem download = event.getDownloadItem();
                        if (download.isCompleted()) {
                            System.out.println("Download is completed!");
                        }
                    }
                });
                System.out.println("Destination file: " +
                        download.getDestinationFile().getAbsolutePath());
                return true;
            }
        });

        browser.loadURL("ftp://ftp.teamdev.com/updates/jxbrowser-4.0-beta.zip");
    }
}
