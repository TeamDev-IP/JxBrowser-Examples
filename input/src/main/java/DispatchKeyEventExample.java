/*
 *  Copyright 2020, TeamDev. All rights reserved.
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

import com.google.common.collect.ImmutableMap;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.navigation.event.FrameLoadFinished;
import com.teamdev.jxbrowser.ui.KeyCode;
import com.teamdev.jxbrowser.ui.event.KeyPressed;
import com.teamdev.jxbrowser.ui.event.KeyReleased;
import com.teamdev.jxbrowser.ui.event.KeyTyped;
import com.teamdev.jxbrowser.view.swing.BrowserView;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static com.google.common.util.concurrent.Uninterruptibles.awaitUninterruptibly;
import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;

/**
 * This example demonstrates how to dispatch the {@code KeyEvent} to
 * the currently focused element on the loaded web page.
 */
public final class DispatchKeyEventExample {

    private static final String HTML = "<input id=\"input\" autofocus>";
    private static final Map<Character, KeyCode> charToKeyCode;

    static {
        charToKeyCode = ImmutableMap.<Character, KeyCode>builder()
                .put('h', KeyCode.KEY_CODE_H)
                .put('i', KeyCode.KEY_CODE_I)
                .build();
    }

    public static void main(String[] args) {
        Engine engine = Engine.newInstance(EngineOptions.newBuilder(HARDWARE_ACCELERATED).build());
        Browser browser = engine.newBrowser();

        SwingUtilities.invokeLater(() -> {
            BrowserView view = BrowserView.newInstance(browser);
            JFrame frame = new JFrame("Dispatch key event");
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
        dispatchKeyH(browser);
        dispatchKeyI(browser);
    }

    private static void dispatchKeyH(Browser browser) {
        dispatchKeyEvent(browser, 'h', charToKeyCode.get('h'));
    }

    private static void dispatchKeyI(Browser browser) {
        dispatchKeyEvent(browser, 'i', charToKeyCode.get('i'));
    }

    private static void loadHtmlAndWait(Browser browser, String html) {
        CountDownLatch latch = new CountDownLatch(1);
        browser.navigation().on(FrameLoadFinished.class, event -> latch.countDown());
        browser.mainFrame().ifPresent(frame -> frame.loadHtml(html));
        awaitUninterruptibly(latch);
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
