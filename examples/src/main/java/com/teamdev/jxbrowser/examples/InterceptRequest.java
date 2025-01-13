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

import static com.teamdev.jxbrowser.engine.RenderingMode.OFF_SCREEN;
import static javax.swing.SwingUtilities.invokeLater;

import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.net.HttpHeader;
import com.teamdev.jxbrowser.net.HttpStatus;
import com.teamdev.jxbrowser.net.Scheme;
import com.teamdev.jxbrowser.net.UrlRequestJob;
import com.teamdev.jxbrowser.net.callback.InterceptUrlRequestCallback;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * This example demonstrates how to intercept a URL request and override the response data in the
 * separate thread.
 */
public final class InterceptRequest {

    public static void main(String[] args) {
        var engine = Engine.newInstance(
                EngineOptions.newBuilder(OFF_SCREEN)
                        .addScheme(Scheme.HTTPS, new InterceptHttpsCallback())
                        .build());
        var browser = engine.newBrowser();

        invokeLater(() -> {
            var view = BrowserView.newInstance(browser);

            var frame = new JFrame("Intercept Request");
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

        browser.navigation().loadUrl("https://html5test.teamdev.com/");
    }

    private static final class InterceptHttpsCallback implements InterceptUrlRequestCallback {

        @Override
        public Response on(Params params) {
            var job = params.newUrlRequestJob(
                    UrlRequestJob.Options
                            .newBuilder(HttpStatus.OK)
                            .addHttpHeader(HttpHeader.of("Content-Type", "text/html"))
                            .addHttpHeader(HttpHeader.of("Content-Type", "charset=utf-8"))
                            .build());
            // Perform complex calculations and override the response data in the separate thread.
            new Thread(() -> {
                job.write("<html><body><h1>Hello there!</h1></body></html>".getBytes());
                job.complete();
            }).start();
            return Response.intercept(job);
        }
    }
}
