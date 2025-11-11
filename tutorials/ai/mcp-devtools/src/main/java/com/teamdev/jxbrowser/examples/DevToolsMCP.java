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

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.view.swing.BrowserView;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.net.URISyntaxException;

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;
import static javax.swing.SwingConstants.CENTER;
import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 * An example app that demonstrates how to automate JxBrowser using
 * the Chrome DevTools Protocol and a Playwright MCP server.
 *
 * <p>This example:
 * <ol>
 *     <li>Starts a Chromium engine with remote debugging enabled.</li>
 *     <li>Opens a JFrame with a BrowserView inside.</li>
 *     <li>Loads a web page and connects to the Playwright MCP server via the DevTools Protocol.</li>
 * </ol>
 */
public final class DevToolsMCP {

    /**
     * The remote debugging URL used by default.
     */
    private static final String DEBUGGING_URL = "http://localhost:9222";

    /**
     * The initial URL to load in the browser.
     */
    private static final String START_URL = "https://teamdev.com";

    public static void main(String[] args) {
        var debuggingUrl = System.getProperty("debugging.url", DEBUGGING_URL);
        var debuggingPort = extractPort(debuggingUrl);
        var engine = Engine.newInstance(
                EngineOptions.newBuilder(HARDWARE_ACCELERATED)
                        .addSwitch("--remote-allow-origins=" + debuggingUrl)
                        .remoteDebuggingPort(debuggingPort)
                        .build());
        var browser = engine.newBrowser();

        showUI(browser);
        browser.navigation().loadUrl(START_URL);
    }

    private static int extractPort(String url) {
        try {
            URI uri = new URI(url);
            int port = uri.getPort();
            return port > 0 ? port : 9222;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid debugging URL: " + url, e);
        }
    }

    private static void showUI(Browser browser) {
        invokeLater(() -> {
            var frame = new JFrame("JxBrowser DevTools MCP");
            frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            frame.setSize(1280, 900);
            frame.setLocationRelativeTo(null);
            frame.add(BrowserView.newInstance(browser), CENTER);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    browser.engine().close();
                }
            });
            frame.setVisible(true);
        });
    }
}
