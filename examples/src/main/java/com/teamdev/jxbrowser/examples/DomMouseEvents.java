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

import static com.google.common.util.concurrent.Uninterruptibles.awaitUninterruptibly;
import static com.teamdev.jxbrowser.dom.event.EventType.MOUSE_DOWN;
import static com.teamdev.jxbrowser.dom.event.EventType.MOUSE_OVER;
import static com.teamdev.jxbrowser.dom.event.EventType.MOUSE_UP;
import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;
import static java.lang.String.format;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.dom.Element;
import com.teamdev.jxbrowser.dom.event.Event;
import com.teamdev.jxbrowser.dom.event.MouseEvent;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.frame.Frame;
import com.teamdev.jxbrowser.navigation.event.FrameLoadFinished;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * This examples demonstrates how to capture mouse events from the DOM nodes.
 */
public class DomMouseEvents {

    private static final String BUTTON_ID = "button";
    private static final String HTML = "<button id='" + BUTTON_ID + "'>Click me</button>";

    public static void main(String[] args) {
        var options = EngineOptions.newBuilder(HARDWARE_ACCELERATED).build();
        var engine = Engine.newInstance(options);
        var browser = engine.newBrowser();

        SwingUtilities.invokeLater(() -> {
            var view = BrowserView.newInstance(browser);

            var frame = new JFrame("DOM Mouse Event Listener");
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    engine.close();
                }
            });
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(view, BorderLayout.CENTER);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
        loadHtmlAndWait(browser);

        findButton(browser).ifPresent(element -> {
            element.addEventListener(MOUSE_DOWN, DomMouseEvents::printEventDetails, false);
            element.addEventListener(MOUSE_UP, DomMouseEvents::printEventDetails, false);
            element.addEventListener(MOUSE_OVER, DomMouseEvents::printEventDetails, false);
        });
    }

    private static void printEventDetails(Event event) {
        var mouseEvent = (MouseEvent) event;
        var location = mouseEvent.pageLocation();
        var message = format("Event type: %s. Button: %s. Page location: (%d, %d)",
                mouseEvent.type().value(), mouseEvent.button(), location.x(), location.y());
        System.out.println(message);
    }

    private static void loadHtmlAndWait(Browser browser) {
        var latch = new CountDownLatch(1);
        browser.navigation().on(FrameLoadFinished.class, event -> latch.countDown());
        browser.navigation().loadHtml(HTML);
        awaitUninterruptibly(latch);
    }

    private static Optional<Element> findButton(Browser browser) {
        return browser.mainFrame()
                .flatMap(Frame::document)
                .flatMap(document -> document.findElementById(BUTTON_ID));
    }
}
