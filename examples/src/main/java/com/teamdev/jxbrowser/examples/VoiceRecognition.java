/*
 *  Copyright 2025, TeamDev. All rights reserved.
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
import static com.teamdev.jxbrowser.permission.PermissionType.AUDIO_CAPTURE;

import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.permission.callback.RequestPermissionCallback;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * This example demonstrates how to enable voice recognition functionality
 * in Chromium engine.
 *
 * <p>By default, the voice recognition functionality is disabled. To enable it,
 * you must provide your Google API keys to the Chromium engine as shown
 * in this example.
 *
 * <p>The instruction that describes how to acquire the API keys can be found
 * <a href="https://chromium.googlesource.com/chromium/src.git/+/HEAD/docs/api_keys.md">here</a>.
 * In particular, to make voice recognition work, Speech API should be enabled.
 */
public final class VoiceRecognition {

    public static void main(String[] args) {
        var engine = Engine.newInstance(
                EngineOptions.newBuilder(HARDWARE_ACCELERATED)
                        .googleApiKey("your_api_key")
                        .googleDefaultClientId("your_client_id")
                        .googleDefaultClientSecret("your_client_secret")
                        .build());

        // Grant access to record audio.
        engine.permissions().set(
                RequestPermissionCallback.class,
                (params, tell) -> {
                    if (params.permissionType() == AUDIO_CAPTURE) {
                        tell.grant();
                    } else {
                        tell.deny();
                    }
                }
        );

        var browser = engine.newBrowser();

        SwingUtilities.invokeLater(() -> {
            var view = BrowserView.newInstance(browser);

            var frame = new JFrame("Voice Recognition");
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    engine.close();
                }
            });
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(view, BorderLayout.CENTER);
            frame.setSize(700, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
        // To test the voice recognition click the "Search by voice" icon in the search field.
        browser.navigation().loadUrl("https://google.com");
    }
}
