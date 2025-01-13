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

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;

import com.teamdev.jxbrowser.dom.Document;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.navigation.event.FrameLoadFinished;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * This example demonstrates how to use {@code querySelector} DOM API.
 */
public final class DomQuerySelector {

    public static void main(String[] args) {
        var engine = Engine.newInstance(HARDWARE_ACCELERATED);
        var browser = engine.newBrowser();

        SwingUtilities.invokeLater(() -> {
            var view = BrowserView.newInstance(browser);

            var frame = new JFrame("DOM Query Selector");
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    engine.close();
                }
            });
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.getContentPane().add(view, BorderLayout.CENTER);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        browser.navigation().on(FrameLoadFinished.class, event ->
                event.frame().document().flatMap(Document::documentElement).ifPresent(element ->
                        element.findElementsByCssSelector("p").forEach(paragraph ->
                                System.out.println("innerHTML " + paragraph.innerHtml()))));
        browser.mainFrame().ifPresent(mainFrame ->
                mainFrame.loadHtml("<html><body><div id='root'>" +
                        "<p>paragraph1</p>" +
                        "<p>paragraph2</p>" +
                        "<p>paragraph3</p>" +
                        "</div></body></html>"));
    }
}
