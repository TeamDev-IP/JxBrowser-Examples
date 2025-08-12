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

import com.teamdev.jxbrowser.browser.callback.InjectJsCallback;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.js.JsAccessible;
import com.teamdev.jxbrowser.js.JsObject;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * This example demonstrates how to intercept web socket data by using JS-Java
 * bridget capabilities.
 */
public final class WebSocketDataInterceptor {

    private static final String JAVA_SCRIPT = """
            var oldSocket = window.WebSocket;
            window.WebSocket = function (url) {
                    var socket = new oldSocket(url);
                    socket.onopen = () => {
                        window.webSocketCallback.socketOpened(socket);
                        this.onopen();
                    };
                    socket.onmessage = (message) => {
                        window.webSocketCallback.messageReceived(socket,message.data);
                        this.onmessage(message);
                    };
                    var onclose = socket.onclose;
                    socket.onclose = (closeEvent) => {
                        this.onclose();
                        window.webSocketCallback.socketClosed(closeEvent);
                        this.close(closeEvent);
                    };
                    this.close = (event) => {
                        window.webSocketCallback.socketClosed(event);
                        socket.close();
                    };
                    this.send = (data) => {
                        window.webSocketCallback.beforeSendData(socket,data);
                        socket.send(data);
                    };
                };""";

    private static String getUrl() {
        ClassLoader classLoader = WebSocketDataInterceptor.class.getClassLoader();
        if (classLoader != null) {
            URL resource = classLoader.getResource("echo.html");
            if (resource != null) {
                return resource.toString();
            }
        }
        throw new IllegalArgumentException("Resource not found: echo.html");
    }

    public static void main(String[] args) {
        var engine = Engine.newInstance(HARDWARE_ACCELERATED);
        var browser = engine.newBrowser();
        browser.set(InjectJsCallback.class, params -> {
            var frame = params.frame();
            JsObject jsObject = frame.executeJavaScript("window");
            if (jsObject != null) {
                jsObject.putProperty("webSocketCallback",
                        new WebSocketCallback());
            }
            frame.executeJavaScript(JAVA_SCRIPT);
            return InjectJsCallback.Response.proceed();
        });

        SwingUtilities.invokeLater(() -> {
            var view = BrowserView.newInstance(browser);

            var frame = new JFrame("File Upload");
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

        browser.navigation().loadUrlAndWait(getUrl());
    }

    /**
     * A JS-accessible class used as a web socket event listener in JS.
     *
     * <p>The class is marked with the {@code @JsAccessible} annotation to tell
     * JavaScript that all public methods of the injected object can be invoked
     * from the JavaScript side.
     *
     * <p>Please note, only public classes and static nested classes can be
     * injected into JavaScript.
     */
    @JsAccessible
    public final static class WebSocketCallback {

        @SuppressWarnings("unused") // To be called from JavaScript.
        public void socketClosed(JsObject closeEvent) {
            System.out.println("WebSocketCallback.socketClosed");
        }

        @SuppressWarnings("unused") // To be called from JavaScript.
        public void messageReceived(JsObject socket, Object data) {
            System.out.println("WebSocketCallback.messageReceived: " + data);
        }

        @SuppressWarnings("unused") // To be called from JavaScript.
        public void socketOpened(JsObject socket) {
            System.out.println("WebSocketCallback.socketOpened");
        }

        @SuppressWarnings("unused") // To be called from JavaScript.
        public void beforeSendData(JsObject socket, Object data) {
            System.out.println("WebSocketCallback.beforeSendData: " + data);
        }
    }
}
