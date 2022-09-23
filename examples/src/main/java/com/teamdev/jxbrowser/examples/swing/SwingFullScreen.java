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

package com.teamdev.jxbrowser.examples.swing;

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.fullscreen.FullScreen;
import com.teamdev.jxbrowser.fullscreen.event.FullScreenEntered;
import com.teamdev.jxbrowser.fullscreen.event.FullScreenExited;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.BorderLayout;
import java.awt.Frame;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * This example demonstrates how to switch between fullscreen mode and window mode.
 */
public final class SwingFullScreen {

    public static void main(String[] args) {
        Engine engine = Engine.newInstance(HARDWARE_ACCELERATED);
        Browser browser = engine.newBrowser();

        SwingUtilities.invokeLater(() -> {
            BrowserView view = BrowserView.newInstance(browser);

            JFrame frame = new JFrame("Swing Full Screen Mode");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(view, BorderLayout.CENTER);
            frame.setSize(700, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            FullScreenHandler fullScreenHandler = new FullScreenHandler(frame, view);
            FullScreen fullScreen = browser.fullScreen();
            fullScreen.on(FullScreenEntered.class, event -> fullScreenHandler.onFullScreenEnter());
            fullScreen.on(FullScreenExited.class, event -> fullScreenHandler.onFullScreenExit());
        });

        browser.navigation().loadUrl("http://www.quirksmode.org/html5/tests/video.html");
    }

    private static class FullScreenHandler {

        private final JFrame parentFrame;
        private final BrowserView view;
        private final JFrame frame;

        private FullScreenHandler(JFrame parentFrame, BrowserView view) {
            this.parentFrame = parentFrame;
            this.view = view;
            this.frame = createFrame();
        }

        private static JFrame createFrame() {
            JFrame frame = new JFrame();
            frame.setUndecorated(true);
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
            return frame;
        }

        void onFullScreenEnter() {
            SwingUtilities.invokeLater(() -> {
                parentFrame.remove(view);
                frame.add(view);
                frame.setVisible(true);
                parentFrame.setVisible(false);
            });
        }

        void onFullScreenExit() {
            SwingUtilities.invokeLater(() -> {
                frame.remove(view);
                parentFrame.add(view);
                parentFrame.setVisible(true);
                frame.setVisible(false);
            });
        }
    }
}
