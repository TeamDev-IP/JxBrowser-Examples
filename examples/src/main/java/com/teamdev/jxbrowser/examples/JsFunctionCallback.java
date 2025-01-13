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

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.js.JsObject;

/**
 * This example demonstrates how to inject {@code JsFunctionCallback} into JavaScript and call it
 * from JavaScript code.
 */
public final class JsFunctionCallback {

    public static void main(String[] args) {
        try (Engine engine = Engine.newInstance(OFF_SCREEN)) {

            Browser browser = engine.newBrowser();
            browser.mainFrame().ifPresent(frame -> {
                JsObject jsObject = frame.executeJavaScript("window");
                if (jsObject != null) {
                    // Inject JsFunctionCallback into JavaScript and associate it
                    // with the "window.sayHelloTo" JavaScript property.
                    jsObject.putProperty("sayHelloTo",
                            (com.teamdev.jxbrowser.js.JsFunctionCallback) arguments ->
                                    "Hello, " + arguments[0]);
                }
                String greetings = frame.executeJavaScript("window.sayHelloTo('John')");
                System.out.println(greetings);
            });
        }
    }
}
