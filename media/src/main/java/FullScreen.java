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
import com.teamdev.jxbrowser.chromium.BrowserType;
import com.teamdev.jxbrowser.chromium.FullScreenHandler;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import java.awt.BorderLayout;
import java.awt.Frame;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Demonstrates how to switch between fullscreen mode and window mode.
 */
public class FullScreen {
    public static void main(String[] args) {
        Browser browser = new Browser(BrowserType.LIGHTWEIGHT);
        BrowserView view = new BrowserView(browser);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(view, BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        browser.setFullScreenHandler(new MyFullScreenHandler(frame, view));

        browser.loadURL("http://www.quirksmode.org/html5/tests/video.html");
    }

    private static class MyFullScreenHandler implements FullScreenHandler {
        private final JFrame parentFrame;
        private final BrowserView view;
        private final JFrame frame;

        private MyFullScreenHandler(JFrame parentFrame, BrowserView view) {
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

        @Override
        public void onFullScreenEnter() {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    parentFrame.getContentPane().remove(view);
                    frame.getContentPane().add(view);
                    frame.setVisible(true);
                    parentFrame.setVisible(false);
                }
            });
        }

        @Override
        public void onFullScreenExit() {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    frame.getContentPane().remove(view);
                    parentFrame.getContentPane().add(view);
                    parentFrame.setVisible(true);
                    frame.setVisible(false);
                }
            });
        }
    }
}
