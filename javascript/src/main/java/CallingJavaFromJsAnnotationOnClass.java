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
import com.teamdev.jxbrowser.browser.event.ConsoleMessageReceived;
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
import static com.teamdev.jxbrowser.js.ConsoleMessageLevel.LEVEL_ERROR;
import static java.lang.String.format;

/**
 * This example demonstrates how to call Java methods from JavaScript using injecting Java object
 * and {@code @JsAccessible} annotation on injected class.
 */
public final class CallingJavaFromJsAnnotationOnClass {

    public static void main(String[] args) {
        Engine engine = Engine.newInstance(
                EngineOptions.newBuilder(HARDWARE_ACCELERATED).build());
        Browser browser = engine.newBrowser();

        SwingUtilities.invokeLater(() -> {
            BrowserView view = BrowserView.newInstance(browser);

            JFrame frame = new JFrame("Java object injection. Annotation on class");
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

        browser.on(ConsoleMessageReceived.class, event -> {
            if (event.consoleMessage().level().equals(LEVEL_ERROR)) {
                // Error message will be displayed if non-public or not annotated java method is called.
                System.out.println(event.consoleMessage().message());
            }
        });

        Navigation navigation = browser.navigation();
        navigation.on(FrameLoadFinished.class, event -> {
            String js = "window.java.sayGreetings();";
            event.frame().executeJavaScript(js);
            js = "window.java.saySomething('Please hear me!!!');";
            event.frame().executeJavaScript(js);
            js = "window.java.sayNothing();";
            event.frame().executeJavaScript(js);
        });

        navigation.loadUrl("about:blank");
    }

    public static class BaseClass {

        // Not accessible from the annotated inheritor.
        public void sayNothing() {
            System.out.println("(silence)");
        }
    }

    @JsAccessible // All the public declared methods in the class are accessible.
    public static class JavaObject extends BaseClass {

        public void sayGreetings() {
            System.out.println("Hello World!");
        }

        // Not public method is not accessible.
        void saySomething(String s) { // Annotated not public method is inaccessible.
            System.out.println(s);
        }
    }
}
