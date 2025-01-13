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

import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.js.JsAccessible;
import com.teamdev.jxbrowser.js.JsObject;

/**
 * This example demonstrates how to inject a Java object into JavaScript,
 * and make some of its public methods accessible from JavaScript.
 */
public final class JsAccessibleMethod {

    public static void main(String[] args) {
        try (var engine = Engine.newInstance(OFF_SCREEN)) {
            var browser = engine.newBrowser();
            browser.mainFrame().ifPresent(frame -> {
                JsObject jsObject = frame.executeJavaScript("window");
                if (jsObject != null) {
                    // Inject Java object into JavaScript and associate it
                    // with the "window.java" JavaScript property.
                    jsObject.putProperty("java", new JavaObject());
                }
                // Call the annotated public method of the injected Java object from JS.
                frame.executeJavaScript("window.java.sayHello()");
            });
        }
    }

    // Only public classes and static nested classes can be injected into JS.
    public static final class JavaObject {

        @JsAccessible // This public method is accessible from JS.
        @SuppressWarnings("unused") // To be called from JavaScript.
        public void sayHello() {
            System.out.println("Hello!");
        }
    }
}
