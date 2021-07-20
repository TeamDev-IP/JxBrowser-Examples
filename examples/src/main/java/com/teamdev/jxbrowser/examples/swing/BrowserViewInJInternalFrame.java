/*
 *  Copyright 2021, TeamDev. All rights reserved.
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

package com.teamdev.jxbrowser.examples.swing;

import static com.teamdev.jxbrowser.engine.RenderingMode.OFF_SCREEN;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.BorderLayout;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * This example demonstrates how to display Swing BrowserView in JInternalFrame.
 */
public final class BrowserViewInJInternalFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JDesktopPane pane = new JDesktopPane();
            pane.add(createInternalFrame("TeamDev", "http://www.teamdev.com", 0));
            pane.add(createInternalFrame("Google", "http://www.google.com", 100));

            JFrame frame = new JFrame("BrowserView In JInternalFrame");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(pane, BorderLayout.CENTER);
            frame.setSize(800, 800);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static JInternalFrame createInternalFrame(String title, String url, int offset) {
        // To display BrowserView in Swing JInternalFrame, the engine must be configured
        // with the OFF_SCREEN rendering mode. In case of the HARDWARE_ACCELERATED rendering
        // mode we will get a well known issue with mixing heavyweight and lightweight
        // components. Read more about this limitation at
        // https://jxbrowser-support.teamdev.com/docs/guides/browser-view.html#mixing-heavyweight-and-lightweight
        Engine engine = Engine.newInstance(OFF_SCREEN);
        Browser browser = engine.newBrowser();
        browser.navigation().loadUrl(url);

        BrowserView view = BrowserView.newInstance(browser);

        JInternalFrame frame = new JInternalFrame(title, true);
        frame.setContentPane(view);
        frame.setLocation(100 + offset, 100 + offset);
        frame.setSize(400, 400);
        frame.setVisible(true);
        return frame;
    }
}
