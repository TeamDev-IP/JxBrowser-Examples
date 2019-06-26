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
import com.teamdev.jxbrowser.frame.Frame;
import com.teamdev.jxbrowser.js.JsAccessible;
import com.teamdev.jxbrowser.js.JsObject;
import com.teamdev.jxbrowser.navigation.Navigation;
import com.teamdev.jxbrowser.navigation.event.FrameLoadFinished;
import com.teamdev.jxbrowser.view.swing.BrowserView;

import javax.swing.*;
import java.awt.*;

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;
import static java.lang.String.format;

/**
 * This example demonstrates how to call Java methods from JavaScript using injecting Java object
 * and {@code @JsAccessible} annotation.
 */
public final class CallingJavaMethodsFromJs {

    public static void main(String[] args) {
        Engine engine = Engine.newInstance(
                EngineOptions.newBuilder(HARDWARE_ACCELERATED).build());
        Browser browser = engine.newBrowser();

        SwingUtilities.invokeLater(() -> {
            BrowserView view = BrowserView.newInstance(browser);

            JFrame frame = new JFrame("Java object injection");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(view, BorderLayout.CENTER);
            frame.setSize(700, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        browser.set(InjectJsCallback.class, params -> {
            Frame frame = params.frame();
            String window = "window";
            JsObject jsObject = frame.executeJavaScript(window);
            if (jsObject == null) {
                throw new IllegalStateException(
                        format("'%s' JS object not found", window));
            }
            jsObject.putProperty("java", new JavaObject());
            return InjectJsCallback.Response.proceed();
        });

        Navigation navigation = browser.navigation();
        navigation.on(FrameLoadFinished.class, event -> {
            String js = "window.java.sayHello();" +
                    "window.java.sayWorld();" +
                    "window.java.saySomething('!');";
            event.frame().executeJavaScript(js);
        });

        navigation.loadUrl("about:blank");
    }

    @SuppressWarnings("unused") // Methods are invoked via JavaScript.
    @JsAccessible /* All the public methods are accessible from this
                     class and the inheritors if they are not overridden.*/
    public static class BaseClass {

        public void sayHello() {
            System.out.print("Hello ");
        }

        public void saySomething(String s) {
            System.out.print("something");
        }

        // Not public method is not accessible at all.
        protected void sayNothing() {
            System.out.print("(silence)");
        }
    }

    public static class JavaObject extends BaseClass {

        @JsAccessible // Annotated public method is accessible.
        public void sayWorld() {
            System.out.print("World");
        }

        @Override
        @JsAccessible // Without this annotation overridden method is not accessible.
        public void saySomething(String s) {
            System.out.print(s);
        }
    }

}
