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
import com.teamdev.jxbrowser.chromium.LoggerProvider;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.logging.*;

/**
 * This example demonstrates how to redirect all JxBrowser log
 * messages to the '*.log' files.
 */
public class RedirectLoggingToFile {
    public static void main(String[] args) throws IOException {
        LoggerProvider.setLevel(Level.ALL);

        // Redirect Browser log messages to jxbrowser-browser.log
        redirectLogMessagesToFile(LoggerProvider.getBrowserLogger(),
                "jxbrowser-browser.log");

        // Redirect IPC log messages to jxbrowser-ipc.log
        redirectLogMessagesToFile(LoggerProvider.getIPCLogger(),
                "jxbrowser-ipc.log");

        // Redirect Chromium Process log messages to jxbrowser-chromium.log
        redirectLogMessagesToFile(LoggerProvider.getChromiumProcessLogger(),
                "jxbrowser-chromium.log");

        Browser browser = new Browser();
        BrowserView view = new BrowserView(browser);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(view, BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        browser.loadURL("https://google.com");
    }

    private static void redirectLogMessagesToFile(Logger logger, String logFilePath)
            throws IOException {
        FileHandler fileHandler = new FileHandler(logFilePath);
        fileHandler.setFormatter(new SimpleFormatter());

        // Remove default handlers including console handler
        for (Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }
        logger.addHandler(fileHandler);
    }
}
