/*
 *  Copyright 2026, TeamDev. All rights reserved.
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
import com.teamdev.jxbrowser.browser.callback.OpenExtensionActionPopupCallback;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.extensions.Extension;
import com.teamdev.jxbrowser.extensions.ExtensionAction;
import com.teamdev.jxbrowser.navigation.event.FrameDocumentLoadFinished;
import com.teamdev.jxbrowser.view.swing.BrowserView;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;
import static javax.swing.SwingConstants.CENTER;
import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 * An example app that demonstrates how to automate JxBrowser using
 * the Browser MCP Chrome extension and a local MCP server.
 *
 * <p>This example:
 * <ol>
 *     <li>Installs the Browser MCP Chrome extension in JxBrowser.</li>
 *     <li>Opens a JFrame with a BrowserView inside.</li>
 *     <li>Loads a web page and connects to the Browser MCP server via the extension popup.</li>
 * </ol>
 */
public final class ExtensionMCP {

    /**
     * Path to the extension CRX file in the resources folder.
     */
    private static final String EXTENSION_FILE = "browser-mcp.crx";

    /**
     * The initial URL to load in the browser.
     */
    private static final String START_URL = "https://teamdev.com";

    public static void main(String[] args) {
        var engine = Engine.newInstance(
                EngineOptions.newBuilder(HARDWARE_ACCELERATED)
                        .userDataDir(Paths.get("userData"))
                        .build());
        var browser = engine.newBrowser();
        var extension = installExtension(browser);

        showUI(browser);

        browser.navigation().loadUrlAndWait(START_URL);
        extension.action(browser).ifPresent(ExtensionAction::click);
    }

    private static Extension installExtension(Browser browser) {
        var profile = browser.profile();
        var extensions = profile.extensions();

        Path extensionPath = getExtensionPath(EXTENSION_FILE);
        Extension extension = extensions.install(extensionPath);

        browser.set(OpenExtensionActionPopupCallback.class, (params, tell) -> {
            var popupBrowser = params.popupBrowser();
            popupBrowser.navigation().on(FrameDocumentLoadFinished.class, event -> {
                // Auto-click the "Connect" button.
                event.frame().executeJavaScript(
                        "setTimeout(() => document.querySelector('button.w-full')?.click(), 1000);"
                );
            });
            tell.proceed();
        });

        return extension;
    }

    private static void showUI(Browser browser) {
        invokeLater(() -> {
            var frame = new JFrame("JxBrowser Extension MCP");
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

    /**
     * Extracts the extension CRX file to a temporary location on disk, and returns
     * the absolute file system path for installation.
     *
     * <p>We intentionally avoid using {@code getResource().toURI()} to locate the extension
     * file directly, because it doesn't work when the application is packaged into a JAR.
     */
    private static Path getExtensionPath(String extensionFileName) {
        var resource = ExtensionMCP.class.getClassLoader().getResourceAsStream(extensionFileName);
        if (resource == null) {
            throw new IllegalStateException("Missing Chrome extension CRX file: " + extensionFileName);
        }
        try {
            Path tempFile = Files.createTempFile("extension-", ".crx");
            tempFile.toFile().deleteOnExit();
            Files.copy(resource, tempFile, StandardCopyOption.REPLACE_EXISTING);
            return tempFile;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to extract Chrome extension CRX file from resources", e);
        }
    }
}
