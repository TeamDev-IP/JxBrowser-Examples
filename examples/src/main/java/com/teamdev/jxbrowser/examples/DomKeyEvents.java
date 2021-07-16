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

package com.teamdev.jxbrowser.examples;

import static com.google.common.util.concurrent.Uninterruptibles.awaitUninterruptibly;
import static com.teamdev.jxbrowser.dom.event.EventType.KEY_DOWN;
import static com.teamdev.jxbrowser.dom.event.EventType.KEY_PRESS;
import static com.teamdev.jxbrowser.dom.event.EventType.KEY_UP;
import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;
import static java.lang.String.format;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.dom.Element;
import com.teamdev.jxbrowser.dom.event.Event;
import com.teamdev.jxbrowser.dom.event.KeyEvent;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.frame.Frame;
import com.teamdev.jxbrowser.navigation.event.FrameLoadFinished;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.BorderLayout;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * This examples demonstrates how to capture keyboard events from the DOM nodes.
 */
public class DomKeyEvents {

    private static final String INPUT_FIELD_ID = "input-field";
    private static final String HTML = "<input type='text' id='" + INPUT_FIELD_ID + "' />";

    public static void main(String[] args) {
        EngineOptions options = EngineOptions.newBuilder(HARDWARE_ACCELERATED).build();
        Engine engine = Engine.newInstance(options);
        Browser browser = engine.newBrowser();

        SwingUtilities.invokeLater(() -> {
            BrowserView view = BrowserView.newInstance(browser);

            JFrame frame = new JFrame("DOM Keyboard Event Listener ");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(view, BorderLayout.CENTER);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
        loadHtmlAndWait(browser);

        findInputField(browser).ifPresent(element -> {
            element.addEventListener(KEY_DOWN, DomKeyEvents::printEventDetails, false);
            element.addEventListener(KEY_PRESS, DomKeyEvents::printEventDetails, false);
            element.addEventListener(KEY_UP, DomKeyEvents::printEventDetails, false);
        });
    }

    private static void printEventDetails(Event event) {
        KeyEvent keyEvent = (KeyEvent) event;
        String message = format("Event type: %s. Typed character (if applicable): %s. Key: %s",
                keyEvent.type().value(), keyEvent.character(), keyEvent.domKeyCode());
        System.out.println(message);
    }

    private static void loadHtmlAndWait(Browser browser) {
        CountDownLatch latch = new CountDownLatch(1);
        browser.navigation().on(FrameLoadFinished.class, event -> latch.countDown());
        LoadHtml.loadHtmlAndWait(browser, HTML);
        awaitUninterruptibly(latch);
    }

    private static Optional<Element> findInputField(Browser browser) {
        return browser.mainFrame()
                .flatMap(Frame::document)
                .flatMap(document -> document.findElementById(INPUT_FIELD_ID));
    }
}
