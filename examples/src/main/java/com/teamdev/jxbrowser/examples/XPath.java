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

package com.teamdev.jxbrowser.examples;

import static com.teamdev.jxbrowser.engine.RenderingMode.OFF_SCREEN;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.dom.Document;
import com.teamdev.jxbrowser.dom.XPathException;
import com.teamdev.jxbrowser.dom.XPathResult;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.navigation.Navigation;
import com.teamdev.jxbrowser.navigation.event.FrameLoadFinished;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * This example demonstrates how to evaluate the XPath expression and work with the result.
 */
public final class XPath {

    public static void main(String[] args) {
        Engine engine = Engine.newInstance(OFF_SCREEN);
        Browser browser = engine.newBrowser();

        SwingUtilities.invokeLater(() -> {
            BrowserView view = BrowserView.newInstance(browser);

            JFrame frame = new JFrame("Evaluate XPath");
            frame.getContentPane().add(view, BorderLayout.CENTER);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        Navigation navigation = browser.navigation();
        navigation.on(FrameLoadFinished.class, event ->
                event.frame().document().flatMap(Document::documentElement).ifPresent(element -> {
                    try {
                        XPathResult result = element.evaluate("count(//div)");
                        if (result.isNumber()) {
                            System.out.println("Result: " + result.asNumber());
                        }
                    } catch (XPathException e) {
                        System.out.println(e.getMessage());
                    }
                }));

        navigation.loadUrl("https://www.teamdev.com/jxbrowser");
    }
}
