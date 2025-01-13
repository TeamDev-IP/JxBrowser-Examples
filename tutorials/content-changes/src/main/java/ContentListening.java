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

// #docfragment "without-license"

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.base.Charsets;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.browser.callback.InjectJsCallback;
import com.teamdev.jxbrowser.browser.callback.InjectJsCallback.Response;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.frame.Frame;
import com.teamdev.jxbrowser.js.JsAccessible;
import com.teamdev.jxbrowser.js.JsObject;
import com.teamdev.jxbrowser.navigation.event.FrameLoadFinished;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.BorderLayout;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * This example demonstrates how to listen to DOM changes from a Java object.
 */
public final class ContentListening {

    public static void main(String[] args) {
        // #docfragment "engine-creation"
        Engine engine = Engine.newInstance(
                EngineOptions.newBuilder(HARDWARE_ACCELERATED).build());
        Browser browser = engine.newBrowser();
        // #enddocfragment "engine-creation"
        // #docfragment "embed-browser-view"
        SwingUtilities.invokeLater(() -> {
            BrowserView view = BrowserView.newInstance(browser);

            JFrame frame = new JFrame("Content Listening");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(view, BorderLayout.CENTER);
            frame.setSize(700, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
        // #enddocfragment "embed-browser-view"

        // #docfragment "inject-js"
        browser.set(InjectJsCallback.class, params -> {
            Frame frame = params.frame();
            String window = "window";
            JsObject jsObject = frame.executeJavaScript(window);
            if (jsObject == null) {
                throw new IllegalStateException(
                        format("'%s' JS object not found", window));
            }
            jsObject.putProperty("java", new JavaObject());
            return Response.proceed();
        });
        // #enddocfragment "inject-js"

        // #docfragment "frame-load-finished"
        browser.navigation().on(FrameLoadFinished.class, event -> {
            String javaScript = load("observer.js");
            event.frame().executeJavaScript(javaScript);
        });
        // #enddocfragment "frame-load-finished"

        // #docfragment "load-page"
        String html = load("index.html");
        String base64Html = Base64.getEncoder().encodeToString(html.getBytes(UTF_8));
        String dataUrl = "data:text/html;base64," + base64Html;
        browser.navigation().loadUrl(dataUrl);
        // #enddocfragment "load-page"
    }

    /**
     * Loads a resource content as a string.
     */
    // #docfragment "load-method"
    private static String load(String resourceFile) {
        URL url = ContentListening.class.getResource(resourceFile);
        try (Scanner scanner = new Scanner(url.openStream(),
                Charsets.UTF_8.toString())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load resource " +
                    resourceFile, e);
        }
    }
    // #enddocfragment "load-method"

    /**
     * The object observing DOM changes.
     *
     * <p>The class and methods that are invoked from JavaScript code must be public.
     */
    // #docfragment "java-object"
    public static class JavaObject {

        @SuppressWarnings("unused") // invoked by callback processing code.
        @JsAccessible
        public void onDomChanged(String innerHtml) {
            System.out.println("DOM node changed: " + innerHtml);
        }
    }
    // #enddocfragment "java-object"
}
// #enddocfragment "without-license"
