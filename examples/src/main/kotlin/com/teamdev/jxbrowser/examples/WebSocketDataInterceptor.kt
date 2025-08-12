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
package com.teamdev.jxbrowser.examples

import com.teamdev.jxbrowser.browser.callback.InjectJsCallback
import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.register
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.js.JsAccessible
import com.teamdev.jxbrowser.js.JsObject

/**
 * This example demonstrates how to intercept web socket data by using JS-Java
 * bridget capabilities.
 */
fun main() {
    val engine = Engine(RenderingMode.OFF_SCREEN)
    val browser = engine.newBrowser()
    browser.register(InjectJsCallback { params ->
        val frame = params.frame()
        val jsWindow = frame.executeJavaScript<JsObject>("window")!!
        jsWindow.putProperty("websocketCallback", WebSocketCallback())
        frame.executeJavaScript<Any>(JAVA_SCRIPT)
        InjectJsCallback.Response.proceed()
    })
    browser.navigation()
        .loadUrlAndWait("https://www.teamdev.com/jxbrowser#features")
    engine.close()
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

private const val JAVA_SCRIPT = ("var oldSocket = window.WebSocket;\n"
        + "    window.WebSocket = function (url){\n"
        + "        var socket = new oldSocket(url);\n"
        + "        socket.onopen = () => {\n"
        + "            window.websocketCallback.socketOpened(socket);\n"
        + "            this.onopen();\n"
        + "        };\n"
        + "        socket.onmessage = (message) => {\n"
        + "            window.websocketCallback.messageReceived(socket,message.data);\n"
        + "            this.onmessage(message);\n"
        + "        };\n"
        + "        var onclose = socket.onclose;\n"
        + "        socket.onclose = (closeEvent) => {\n"
        + "            this.onclose();\n"
        + "            window.websocketCallback.socketClosed(closeEvent);\n"
        + "            this.close(closeEvent);\n"
        + "        };\n"
        + "        this.close = (event)=> {socket.close();};\n"
        + "        this.send = (data) => {\n"
        + "            window.websocketCallback.beforeSendData(socket,data);\n"
        + "            socket.send(data);\n"
        + "        };\n"
        + "    };")
