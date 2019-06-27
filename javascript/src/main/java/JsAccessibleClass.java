/*
 *  Copyright 2018, TeamDev. All rights reserved.
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
import com.teamdev.jxbrowser.browser.callback.InjectJsCallback;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.js.JsAccessible;
import com.teamdev.jxbrowser.js.JsObject;
import com.teamdev.jxbrowser.navigation.Navigation;
import com.teamdev.jxbrowser.navigation.event.FrameLoadFinished;

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;

/**
 * This example demonstrates how to call Java methods from JavaScript using injecting Java object
 * and {@code @JsAccessible} annotation on the class.
 */
public final class JsAccessibleClass {

    public static void main(String[] args) {
        Engine engine = Engine.newInstance(
                EngineOptions.newBuilder(HARDWARE_ACCELERATED).build());
        Browser browser = engine.newBrowser();

        browser.set(InjectJsCallback.class, params -> {
            JsObject jsObject = params.frame().executeJavaScript("window");
            jsObject.putProperty("batman", new Batman());
            return InjectJsCallback.Response.proceed();
        });

        Navigation navigation = browser.navigation();
        navigation.on(FrameLoadFinished.class, event ->
                event.frame().executeJavaScript("window.batman.sayHello();" +
                        "window.batman.introduceMyself();"));

        navigation.loadUrl("about:blank"); // Load 'about:blank' to initiate frame loading.
    }

    @JsAccessible
    // All the public methods are accessible even from the inheritors if they do not override them.
    public static class Human {
        public void sayHello() {
            System.out.print("Hello! ");
        }

        public void introduceMyself() {
            System.out.println("I am a boring homo sapiens.");
        }
    }

    public static class Batman extends Human {

        // To access the overridden method from JavaScript annotate it or the class it is declared in.
        @Override
        @JsAccessible
        public void introduceMyself() {
            System.out.println("I'm Batman!!!");
        }
    }
}
