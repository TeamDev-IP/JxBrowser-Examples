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
import com.teamdev.jxbrowser.dom.Node;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.event.Subscription;
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
 * This example demonstrates how to share the primary screen programmatically via WebRTC and
 * generate a URL that can be opened remotely in a browser.
 */
public final class ScreenSharing {

    private static final String WEBRTC_SCREEN_SHARING_URL = "https://www.webrtc-experiment.com/Pluginfree-Screen-Sharing";

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

        invokeLater(() -> {
            JFrame frame = new JFrame("Screen Sharing Example");
            JButton share = new JButton("Share My Screen...");

            share.addActionListener(e -> {

                // Load the WebRTC Screen Sharing Demo
                // and wait until the document in the main frame is loaded completely.
                loadUrlAndWaitForDocument(browser, WEBRTC_SCREEN_SHARING_URL);

                // Click the "Share Your Screen" button to start a capture session.
                browser.mainFrame().flatMap(mainFrame -> mainFrame.document()
                                .flatMap(document -> document.findElementById("share-screen")))
                        .ifPresent(Node::click);

                showSuccessDialog(frame, browser.url());
            });

            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(500, 300);
            frame.setLayout(new GridBagLayout());
            frame.add(share, new GridBagConstraints());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static void loadUrlAndWaitForDocument(Browser browser, String url) {
        CountDownLatch documentLoaded = new CountDownLatch(1);
        Subscription subscription = browser.navigation().on(FrameDocumentLoadFinished.class, e -> {
            if (e.frame().isMain()) {
                documentLoaded.countDown();
            }
        });
        browser.navigation().loadUrlAndWait(url);
        try {
            documentLoaded.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        subscription.unsubscribe();
    }

    private static void showSuccessDialog(JFrame frame, String url) {
        invokeLater(() -> {
            String title = "You are sharing the primary screen";
            String message = String.format(
                    "Please open the following URL in a web browser to see your screen remotely:%n%s",
                    url);
            String copyActionText = "Copy URL";
            String closeActionText = "Close";

            Object[] options = new Object[]{copyActionText, closeActionText};
            int returnValue = JOptionPane.showOptionDialog(frame,
                    message,
                    title,
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (returnValue == JOptionPane.OK_OPTION) {
                copyToClipboard(url);
            }
        });
    }

    private static void copyToClipboard(String url) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(url), null);
    }
}
