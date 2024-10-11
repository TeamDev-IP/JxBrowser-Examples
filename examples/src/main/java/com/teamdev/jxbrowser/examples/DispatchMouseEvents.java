/*
 *  Copyright 2024, TeamDev. All rights reserved.
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

import static com.google.common.util.concurrent.Uninterruptibles.awaitUninterruptibly;
import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.navigation.event.FrameLoadFinished;
import com.teamdev.jxbrowser.ui.MouseButton;
import com.teamdev.jxbrowser.ui.Point;
import com.teamdev.jxbrowser.ui.event.MousePressed;
import com.teamdev.jxbrowser.ui.event.MouseReleased;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CountDownLatch;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * This example demonstrates how to programmatically dispatch the right click mouse event to the
 * currently loaded web page and get the event notification on JavaScript.
 */
public final class DispatchMouseEvents {

    private static final String HTML = "<html><body>\n"
            + "<script>\n"
            + "document.oncontextmenu = function(e) {\n"
            + "  document.body.innerHTML = \"DOM event triggered: \" + e.type;\n"
            + "  return false;\n"
            + "}\n"
            + "</script>\n"
            + "</body></html>";

    public static void main(String[] args) {
        var engine = Engine.newInstance(HARDWARE_ACCELERATED);
        var browser = engine.newBrowser();

        displayBrowserView(browser);
        loadHtmlAndWait(browser, HTML);
    }

    private static void displayBrowserView(Browser browser) {
        SwingUtilities.invokeLater(() -> {
            var view = BrowserView.newInstance(browser);

            JButton dispatchMouseEventBtn = new JButton("Dispatch Mouse Right Click");
            dispatchMouseEventBtn.addActionListener(e -> {
                // Dispatch mouse press and then release to simulate click.
                browser.dispatch(MousePressed.newBuilder(Point.of(50, 50))
                        .button(MouseButton.SECONDARY).clickCount(1)
                        .build());
                browser.dispatch(MouseReleased.newBuilder(Point.of(50, 50))
                        .button(MouseButton.SECONDARY).clickCount(1)
                        .build());
            });

            var frame = new JFrame("Dispatch mouse events");
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    browser.engine().close();
                }
            });
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.add(view, BorderLayout.CENTER);
            frame.add(dispatchMouseEventBtn, BorderLayout.SOUTH);
            frame.setSize(800, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static void loadHtmlAndWait(Browser browser, String html) {
        var latch = new CountDownLatch(1);
        browser.mainFrame().ifPresent(mainFrame -> mainFrame.loadHtml(html));
        browser.navigation().on(FrameLoadFinished.class, event -> latch.countDown());
        awaitUninterruptibly(latch);
    }
}
