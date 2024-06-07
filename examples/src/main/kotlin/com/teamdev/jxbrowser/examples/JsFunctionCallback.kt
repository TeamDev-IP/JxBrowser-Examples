/*
 *  Copyright 2024, TeamDev. All rights reserved.
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

import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.mainFrame
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.js.JsFunctionCallback
import com.teamdev.jxbrowser.js.JsObject

/**
 * This example demonstrates how to inject a Java function into JavaScript.
 */
fun main() {
    val engine = Engine(RenderingMode.OFF_SCREEN)
    val browser = engine.newBrowser()
    val frame = browser.mainFrame!!

    // Inject a Java `sayHello` function to JS `window` object.
    val jsWindow = frame.executeJavaScript<JsObject>("window")!!
    val sayHello = JsFunctionCallback { args -> "Hello, ${args[0]}" }
    jsWindow.putProperty("sayHello", sayHello)

    // Call the injected callback
    val greetings = frame.executeJavaScript<String>("window.sayHello('John')")
    println(greetings)

    engine.close()
}
