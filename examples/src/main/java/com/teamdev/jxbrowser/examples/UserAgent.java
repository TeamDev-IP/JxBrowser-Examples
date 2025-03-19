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

import com.teamdev.jxbrowser.browser.Browser;
import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;

import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.net.UserAgentBrand;
import com.teamdev.jxbrowser.net.UserAgentData;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * This example demonstrates how to configure the {@code Engine} with a custom user agent string
 * and override the user agent hints.
 */
public final class UserAgent {

    public static void main(String[] args) {
        var engine = Engine.newInstance(
                EngineOptions.newBuilder(HARDWARE_ACCELERATED)
                        .userAgent("My User Agent String")
                        .build());
        var browser = engine.newBrowser();
        setUserAgentHints(browser);

        SwingUtilities.invokeLater(() -> {
            var view = BrowserView.newInstance(browser);

            var frame = new JFrame("User Agent");
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    engine.close();
                }
            });
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(view, BorderLayout.CENTER);
            frame.setSize(700, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        browser.navigation().loadUrl("https://www.whatismybrowser.com/detect/what-is-my-user-agent/");
    }

    private static void setUserAgentHints(Browser browser) {
        var data = UserAgentData.newBuilder()
                .addBrand(UserAgentBrand.create("MyBrand", "1"))
                .addBrand(UserAgentBrand.create("MyBrand2", "2"))
                .addFormFactor("MyFormFactor")
                .fullVersion("1.0")
                .platform("MyOS")
                .platformVersion("1.0")
                .architecture("x86")
                .bitness("32")
                .model("MyModel")
                .mobile(true)
                .wow64(true)
                .build();
        browser.userAgentData(data);
    }
}
