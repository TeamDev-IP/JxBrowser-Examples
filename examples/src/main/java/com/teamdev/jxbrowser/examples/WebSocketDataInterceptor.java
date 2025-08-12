/*
 * Copyright 2025, TeamDev. All rights reserved.
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

import com.teamdev.jxbrowser.browser.callback.InjectJsCallback;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.js.JsAccessible;
import com.teamdev.jxbrowser.js.JsObject;

/**
 * This example demonstrates how to intercept web socket data by using JS-Java
 * bridget capabilities.
 */
public class WebSocketDataInterceptor {
    private static final String JAVA_SCRIPT = """
            var oldSocket = window.WebSocket;
                window.WebSocket = function (url){
                    var socket = new oldSocket(url);
                    socket.onopen = () => {
                        window.websocketCallback.socketOpened(socket);
                        this.onopen();
                    };
                    socket.onmessage = (message) => {
                        window.websocketCallback.messageReceived(socket,message.data);
                        this.onmessage(message);
                    };
                    var onclose = socket.onclose;
                    socket.onclose = (closeEvent) => {
                        this.onclose();
                        window.websocketCallback.socketClosed(closeEvent);
                        this.close(closeEvent);
                    };
                    this.close = (event)=> {socket.close();};
                    this.send = (data) => {
                        window.websocketCallback.beforeSendData(socket,data);
                        socket.send(data);
                    };
                };""";

    public static void main(String[] args) {
        try (var engine = Engine.newInstance(OFF_SCREEN)) {
            var browser = engine.newBrowser();
            browser.set(InjectJsCallback.class, params -> {
                var frame = params.frame();
                JsObject jsObject = frame.executeJavaScript("window");
                if (jsObject != null) {
                    jsObject.putProperty("websocketCallback", new WebSocketCallback());
                }
                frame.executeJavaScript(JAVA_SCRIPT);
                return InjectJsCallback.Response.proceed();
            });

            browser.navigation().loadUrlAndWait("https://www.teamdev.com/jxbrowser#features");
        }
    }

    /**
     * A JS-accessible class used as web socket events listener in JS.
     *
     * <p>The class is marked with [JsAccessible] annotation to tell JavaScript
     * that all public methods of the injected object can be invoked from
     * the JavaScript side.
     *
     * <p>Please note, only public classes and static nested classes can be injected
     * into JavaScript.
     */
    @JsAccessible
    private static class WebSocketCallback {
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
