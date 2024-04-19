/*
 *  Copyright 2023, TeamDev. All rights reserved.
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

import com.google.common.collect.ImmutableMap;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.navigation.event.FrameLoadFinished;
import com.teamdev.jxbrowser.ui.KeyCode;
import com.teamdev.jxbrowser.ui.event.KeyPressed;
import com.teamdev.jxbrowser.ui.event.KeyReleased;
import com.teamdev.jxbrowser.ui.event.KeyTyped;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * This example demonstrates how to dispatch the {@code KeyEvent} to the currently focused element
 * on the loaded web page.
 */
public final class DispatchKeyEvents {

    private static final String HTML = "<input id=\"input\" autofocus>";
    private static final Map<Character, KeyCode> charToKeyCode;

    public static void main(String[] args) {
        Engine engine = Engine.newInstance(HARDWARE_ACCELERATED);
        Browser browser = engine.newBrowser();

        SwingUtilities.invokeLater(() -> {
            BrowserView view = BrowserView.newInstance(browser);
            JFrame frame = new JFrame("Dispatch key events");
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    engine.close();
                }
            });
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.add(view);
            frame.setSize(800, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        loadHtmlAndWait(browser, HTML);
        dispatchKey(browser, 'h');
        dispatchKey(browser, 'i');
    }

    static {
        charToKeyCode = ImmutableMap.<Character, KeyCode>builder()
                .put('h', KeyCode.KEY_CODE_H)
                .put('i', KeyCode.KEY_CODE_I)
                .build();
    }

    private static void loadHtmlAndWait(Browser browser, String html) {
        CountDownLatch latch = new CountDownLatch(1);
        browser.mainFrame().ifPresent(mainFrame -> mainFrame.loadHtml(html));
        browser.navigation().on(FrameLoadFinished.class, event -> latch.countDown());
        awaitUninterruptibly(latch);
    }

    private static void dispatchKey(Browser browser, char character) {
        dispatchKeyEvent(browser, character, charToKeyCode.get(character));
    }

    private static void dispatchKeyEvent(Browser browser, char character, KeyCode keyCode) {
        KeyPressed keyPressed = KeyPressed.newBuilder(keyCode)
                .keyChar(character)
                .build();
        KeyTyped keyTyped = KeyTyped.newBuilder(keyCode)
                .keyChar(character)
                .build();
        KeyReleased keyReleased = KeyReleased.newBuilder(keyCode)
                .build();

        browser.dispatch(keyPressed);
        browser.dispatch(keyTyped);
        browser.dispatch(keyReleased);
    }
}
