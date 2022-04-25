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

package com.teamdev.jxbrowser.examples;

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;
import static javax.swing.SwingUtilities.invokeLater;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.browser.callback.StartCaptureSessionCallback;
import com.teamdev.jxbrowser.capture.AudioCaptureMode;
import com.teamdev.jxbrowser.capture.CaptureSource;
import com.teamdev.jxbrowser.capture.CaptureSources;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.navigation.event.FrameDocumentLoadFinished;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.concurrent.CountDownLatch;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

/**
 * This example demonstrates how to share the screen with a button click in the Swing application.
 */
public final class ScreenSharing {

    private static final String WEBRTC_SCREEN_SHARING_URL = "https://www.webrtc-experiment.com/Pluginfree-Screen-Sharing";
    private static CountDownLatch countDownLatch;

    public static void main(String[] args) {

        // Create an Engine and Browser instances.
        Engine engine = Engine.newInstance(HARDWARE_ACCELERATED);
        Browser browser = engine.newBrowser();

        // Handle a request to start a capture session.
        browser.set(StartCaptureSessionCallback.class, (params, tell) -> {
            CaptureSources sources = params.sources();

            // Get the capture source (the first entire screen).
            CaptureSource screen = sources.screens().get(0);

            // Tell the browser instance to start a new capture session with capturing the audio content.
            tell.selectSource(screen, AudioCaptureMode.CAPTURE);
        });

        // Subscribe to the complete document download.
        browser.navigation().on(FrameDocumentLoadFinished.class, event -> {
            String url = event.frame().browser().url();
            if (url.startsWith(WEBRTC_SCREEN_SHARING_URL)) {
                countDownLatch.countDown();
            }
        });

        invokeLater(() -> {
            JFrame frame = new JFrame("Screen Sharing Example");
            JButton share = new JButton("Share Your Screen");

            share.addActionListener(e -> {
                countDownLatch = new CountDownLatch(1);

                // Load the WebRTC Screen Sharing Demo.
                browser.navigation()
                        .loadUrlAndWait(WEBRTC_SCREEN_SHARING_URL);

                // Wait until the document in the main frame is loaded completely.
                try {
                    countDownLatch.await();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }

                // Click the "Share Your Screen" button to start a capture session.
                browser.mainFrame().ifPresent(mainFrame ->
                        mainFrame.executeJavaScript(
                                "document.getElementById('share-screen').click();"));

                // Copy the screen sharing URL to a clipboard.
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new StringSelection(browser.url()), null);
                String message = String.format(
                        "You are sharing the screen.%nURL copied to clipboard.");
                JOptionPane.showMessageDialog(frame, message);
            });

            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLayout(new GridBagLayout());
            frame.add(share, new GridBagConstraints());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
