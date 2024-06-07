/*
 *  Copyright 2023, TeamDev. All rights reserved.
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
import com.teamdev.jxbrowser.js.JsAccessible;
import com.teamdev.jxbrowser.js.JsObject;

/**
 * This example demonstrates how to inject a Java object into JavaScript,
 * and make its public methods accessible from JavaScript.
 */
public final class JsAccessibleClass {

    public static void main(String[] args) {
        try (Engine engine = Engine.newInstance(OFF_SCREEN)) {
            Browser browser = engine.newBrowser();
            browser.mainFrame().ifPresent(frame -> {
                JsObject jsObject = frame.executeJavaScript("window");
                if (jsObject != null) {
                    // Inject Java object into JavaScript and associate it
                    // with the "window.java" JavaScript property.
                    jsObject.putProperty("java", new JavaObject());
                }
                // Call the public method of the injected Java object from JS.
                frame.executeJavaScript("window.java.sayHelloTo('John')");
            });
        }
    }

    // Mark the public class with the @JsAccessible annotation to tell JavaScript
    // that all public methods of the injected Java object can be invoked from
    // the JavaScript side.
    //
    // Only public classes and static nested classes can be injected into JS.
    @JsAccessible
    public static final class JavaObject {

        @SuppressWarnings("unused") // To be called from JavaScript.
        public void sayHelloTo(String firstName) {
            System.out.printf("Hello %s!", firstName);
        }
    }
}
