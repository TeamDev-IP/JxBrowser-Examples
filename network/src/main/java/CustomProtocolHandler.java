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

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.net.*;
import com.teamdev.jxbrowser.net.callback.InterceptRequestCallback;
import com.teamdev.jxbrowser.net.callback.InterceptRequestCallback.Response;
import com.teamdev.jxbrowser.view.swing.BrowserView;

import javax.swing.*;
import java.awt.*;

/**
 * This example demonstrates how to intercept all URL requests and handle a custom protocol.
 */
public final class CustomProtocolHandler {

    private static final String PROTOCOL = "jxb";
    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    private static final String CONTENT_TYPE_HEADER_VALUE = "text/html";

    public static void main(String[] args) {
        Engine engine = Engine.newInstance(
                EngineOptions.newBuilder(RenderingMode.HARDWARE_ACCELERATED).build());
        Browser browser = engine.newBrowser();

        SwingUtilities.invokeLater(() -> {
            BrowserView view = BrowserView.newInstance(browser);

            JFrame frame = new JFrame("Custom Protocol Handler");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(view, BorderLayout.CENTER);
            frame.setSize(700, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        Network network = engine.network();
        network.set(InterceptRequestCallback.class, params -> {
            UrlRequest request = params.urlRequest();
            // If URL protocol equals to the custom "jxb" protocol, then intercept this
            // request and reply with a custom response.
            if (request.url().startsWith(PROTOCOL)) {
                UrlRequestJob job = network.newUrlRequestJob(
                        UrlRequestJob.Options.newBuilder(request.id(), HttpStatus.OK)
                                .addHttpHeader(HttpHeader.of(
                                        CONTENT_TYPE_HEADER_NAME,
                                        CONTENT_TYPE_HEADER_VALUE))
                                .build());
                job.write("<html><body><p>Hello there!</p></body></html>".getBytes());
                job.complete();
                return Response.intercept(job);
            }
            // Otherwise proceed the request using default behavior.
            return Response.proceed();
        });

        browser.navigation().loadUrl(PROTOCOL + "://hello");
    }
}
