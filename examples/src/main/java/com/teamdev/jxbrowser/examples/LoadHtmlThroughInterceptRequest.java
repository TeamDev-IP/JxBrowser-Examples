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

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.net.HttpHeader;
import com.teamdev.jxbrowser.net.HttpStatus;
import com.teamdev.jxbrowser.net.Scheme;
import com.teamdev.jxbrowser.net.UrlRequestJob;
import com.teamdev.jxbrowser.net.callback.InterceptUrlRequestCallback;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * This example demonstrates how to load HTML by intercepting URL requests.
 */
public final class LoadHtmlThroughInterceptRequest {

    public static void main(String[] args) {
        InterceptUrlRequestCallback interceptCallback = params -> {
            if (params.urlRequest().url().endsWith("?load-html")) {
                byte[] bytes = "<html><body>Hello!</body></html>".getBytes();
                UrlRequestJob job = params.newUrlRequestJob(
                        UrlRequestJob.Options
                                .newBuilder(HttpStatus.OK)
                                .addHttpHeader(HttpHeader.of("Content-Type", "text/html"))
                                .build());
                job.write(bytes);
                job.complete();
                return InterceptUrlRequestCallback.Response.intercept(job);
            }
            return InterceptUrlRequestCallback.Response.proceed();
        };

        Engine engine = Engine.newInstance(
                EngineOptions.newBuilder(HARDWARE_ACCELERATED)
                        .addScheme(Scheme.HTTP, interceptCallback)
                        .build());
        Browser browser = engine.newBrowser();

        SwingUtilities.invokeLater(() -> {
            BrowserView view = BrowserView.newInstance(browser);

            JFrame frame = new JFrame("Load Local Files");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(view, BorderLayout.CENTER);
            frame.setSize(700, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        browser.navigation().loadUrl("http://localhost?load-html");
    }
}
