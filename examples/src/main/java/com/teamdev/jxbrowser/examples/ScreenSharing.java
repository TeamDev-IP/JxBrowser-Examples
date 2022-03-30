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
import com.teamdev.jxbrowser.browser.event.CaptureSessionStarted;
import com.teamdev.jxbrowser.capture.AudioCaptureMode;
import com.teamdev.jxbrowser.capture.CaptureSource;
import com.teamdev.jxbrowser.capture.CaptureSources;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * This example demonstrates how to enable screen sharing between two browsers.
 */
public final class ScreenSharing {

    private static final String WEBRTC_SCREEN_SHARING_URL = "https://www.webrtc-experiment.com/Pluginfree-Screen-Sharing/#654705298396222";

    public static void main(String[] args) {

        // Create an Engine and two Browser instances.
        Engine engine = Engine.newInstance(HARDWARE_ACCELERATED);
        Browser browserStreamer = engine.newBrowser();
        Browser browserSpectator = engine.newBrowser();

        // Handle a request to start a capture session.
        browserStreamer.set(StartCaptureSessionCallback.class, (params, tell) -> {
            CaptureSources sources = params.sources();

            // Get the capture source (the first entire screen).
            CaptureSource screen = sources.screens().get(0);

            // Tell the browser instance to start a new capture session with capturing the audio content.
            tell.selectSource(screen, AudioCaptureMode.CAPTURE);
        });

        // Subscribe to the capture session start event.
        browserStreamer.on(CaptureSessionStarted.class, event ->

            // Navigate to the screen sharing URL in the second browser.
            browserSpectator.navigation().loadUrl(WEBRTC_SCREEN_SHARING_URL)
        );

        initBrowserView(browserStreamer);
        initBrowserView(browserSpectator);

        browserStreamer.navigation().loadUrl(WEBRTC_SCREEN_SHARING_URL);
    }

    private static void initBrowserView(Browser browser) {
        invokeLater(() -> {
            BrowserView view = BrowserView.newInstance(browser);

            JFrame frame = new JFrame("Screen Sharing Example");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(view, BorderLayout.CENTER);
            frame.setSize(720, 800);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
