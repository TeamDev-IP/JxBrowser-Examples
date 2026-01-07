/*
 *  Copyright 2026, TeamDev. All rights reserved.
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
package com.teamdev.jxbrowser.examples

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.singleWindowApplication
import com.teamdev.jxbrowser.browser.callback.InjectJsCallback
import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.navigation
import com.teamdev.jxbrowser.dsl.register
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.js.JsAccessible
import com.teamdev.jxbrowser.js.JsObject
import com.teamdev.jxbrowser.view.compose.BrowserView

/**
 * This example demonstrates how to intercept web socket data by using JS-Kotlin
 * bridget capabilities.
 */
fun main() {
    val engine = Engine(RenderingMode.OFF_SCREEN)
    val browser = engine.newBrowser()
    browser.register(InjectJsCallback { params ->
        val frame = params.frame()
        val jsWindow = frame.executeJavaScript<JsObject>("window")!!
        jsWindow.putProperty("webSocketCallback", WebSocketCallback())
        frame.executeJavaScript<Any>(JAVA_SCRIPT)
        InjectJsCallback.Response.proceed()
    })
    singleWindowApplication(title = "Web socket data interceptor") {
        BrowserView(browser)
        LaunchedEffect(Unit) {
            browser.navigation.loadUrlAndWait(getUrl())
        }
    }
}

private fun getUrl(): String {
    val classLoader = WebSocketDataInterceptor::class.java.classLoader
    val resource = classLoader.getResource("echo.html")
    if (resource != null) {
        return resource.toString()
    }
    throw IllegalArgumentException("Resource not found: echo.html")
}

/**
 * A JS-accessible class used as web socket events listener in JS.
 *
 * The class is marked with [JsAccessible] annotation to tell JavaScript
 * that all public methods of the injected object can be invoked from
 * the JavaScript side.
 *
 * Please note, only public classes and static nested classes can be injected
 * into JavaScript.
 */
@JsAccessible
class WebSocketCallback {

    @Suppress("unused") // To be called from JavaScript.
    fun socketClosed(closeEvent: JsObject?) {
        println("WebSocketCallback.socketClosed: $closeEvent")
    }

    @Suppress("unused") // To be called from JavaScript.
    fun messageReceived(socket: JsObject?, data: Any) {
        println("WebSocketCallback.messageReceived: $socket $data")
    }

    @Suppress("unused") // To be called from JavaScript.
    fun socketOpened(socket: JsObject?) {
        println("WebSocketCallback.socketOpened : $socket")
    }

    @Suppress("unused") // To be called from JavaScript.
    fun beforeSendData(socket: JsObject?, data: Any) {
        println("WebSocketCallback.beforeSendData: $socket $data")
    }
}

private val JAVA_SCRIPT = """
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
        };
""".trimIndent()
