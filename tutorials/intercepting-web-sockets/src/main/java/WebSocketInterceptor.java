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

import com.teamdev.jxbrowser.browser.callback.InjectJsCallback;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.frame.Frame;
import com.teamdev.jxbrowser.js.JsFunctionCallback;
import com.teamdev.jxbrowser.js.JsObject;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.BorderLayout;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class WebSocketInterceptor {

    public static void main(String[] args) {
        var engine = Engine.newInstance(RenderingMode.HARDWARE_ACCELERATED);
        var browser = engine.newBrowser();

        // Inject JavaScript and register bridge functions.
        browser.set(InjectJsCallback.class, params -> {
            var frame = params.frame();
            injectInterceptor(frame);
            registerBridgeFunctions(frame);
            return InjectJsCallback.Response.proceed();
        });

        SwingUtilities.invokeLater(() -> {
            var view = BrowserView.newInstance(browser);

            var frame = new JFrame("WebSocket Interceptor");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(view, BorderLayout.CENTER);
            frame.setSize(800, 600);
            frame.setVisible(true);
        });

        // Load the demo HTML page.
        var html = loadResource("websocket-demo.html");
        browser.navigation().loadHtml(html);
    }

    private static void injectInterceptor(Frame frame) {
        var script = loadResource("websocket-interceptor.js");
        frame.executeJavaScript(script);
    }

    private static void registerBridgeFunctions(Frame frame) {
        JsObject window = frame.executeJavaScript("window");

        window.putProperty("onWebSocketReceived", (JsFunctionCallback) args -> {
            var data = (byte[]) args[0];
            System.out.println("[RECEIVED] " + data);
            return null;
        });

        window.putProperty("onWebSocketSent", (JsFunctionCallback) args -> {
            var data = args[0].toString();
            System.out.println("[SENT] " + data);
            return null;
        });
    }

    private static String loadResource(String resourceName) {
        try (var stream = WebSocketInterceptor.class.getResourceAsStream(
                "/" + resourceName)) {
            if (stream == null) {
                throw new RuntimeException(
                        "Resource not found: " + resourceName);
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load resource: " + resourceName, e);
        }
    }
}
