/*
 *  Copyright 2019, TeamDev. All rights reserved.
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

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.net.HttpHeader;
import com.teamdev.jxbrowser.net.HttpStatus;
import com.teamdev.jxbrowser.net.Network;
import com.teamdev.jxbrowser.net.UrlRequestJob;
import com.teamdev.jxbrowser.net.callback.InterceptRequestCallback;
import com.teamdev.jxbrowser.view.swing.BrowserView;

import javax.swing.*;
import java.awt.*;

import static com.teamdev.jxbrowser.engine.RenderingMode.OFF_SCREEN;

/**
 * This example demonstrates how to intercept a URL request and override the responce data in the
 * separate thread.
 */
public final class InterceptRequest {

    public static void main(String[] args) {
        Engine engine = Engine.newInstance(EngineOptions.newBuilder(OFF_SCREEN).build());
        Browser browser = engine.newBrowser();

        Network network = engine.network();
        network.set(InterceptRequestCallback.class, params -> {
            UrlRequestJob urlRequestJob =
                    network.newUrlRequestJob(UrlRequestJob.Options
                            .newBuilder(params.urlRequest().id(), HttpStatus.OK)
                            .addHttpHeader(HttpHeader.of("Content-Type", "text/html"))
                            .addHttpHeader(HttpHeader.of("Content-Type", "charset=utf-8"))
                            .build());
            // Perform complex calculations and override the responce data in the separate thread.
            new Thread(() -> {
                urlRequestJob.write("My data".getBytes());
                urlRequestJob.complete();
            }).start();
            return InterceptRequestCallback.Response.intercept(urlRequestJob);
        });

        SwingUtilities.invokeLater(() -> {
            BrowserView view = BrowserView.newInstance(browser);

            JFrame frame = new JFrame("Intercept Request");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(view, BorderLayout.CENTER);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        browser.navigation().loadUrl("https://www.google.com");
    }
}
