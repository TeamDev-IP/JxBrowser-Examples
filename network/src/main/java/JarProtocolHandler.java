/*
 *  Copyright 2021, TeamDev. All rights reserved.
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

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;
import static com.teamdev.jxbrowser.net.Scheme.JAR;
import static javax.swing.SwingUtilities.invokeLater;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.net.HttpHeader;
import com.teamdev.jxbrowser.net.HttpStatus;
import com.teamdev.jxbrowser.net.Scheme;
import com.teamdev.jxbrowser.net.UrlRequestJob;
import com.teamdev.jxbrowser.net.callback.InterceptUrlRequestCallback;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.BorderLayout;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * This example demonstrates how to register a JAR protocol handler to load the files located inside
 * a JAR archive.
 */
public final class JarProtocolHandler {

    private static final Map<String, String> MIME_TYPE_MAP = new HashMap<>();

    public static void main(String[] args) {
        Engine engine = Engine.newInstance(
                EngineOptions.newBuilder(HARDWARE_ACCELERATED)
                        .addScheme(Scheme.JAR, new InterceptJarRequestCallback())
                        .build());
        Browser browser = engine.newBrowser();

        invokeLater(() -> {
            BrowserView view = BrowserView.newInstance(browser);

            JFrame frame = new JFrame("JAR Protocol Handler");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(view, BorderLayout.CENTER);
            frame.setSize(700, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        // Load the index.html file located inside a JAR archive added to this Java app classpath.
        URL resource = JarProtocolHandler.class.getResource("resources/index.html");
        if (resource != null) {
            browser.navigation().loadUrl(resource.toString());
        }
    }

    static {
        MIME_TYPE_MAP.put(".html", "text/html");
        MIME_TYPE_MAP.put(".js", "text/javascript");
        MIME_TYPE_MAP.put(".css", "text/css");
    }

    /**
     * Converts the "jar://file/path" URL into the "jar:file:/path" URL.
     */
    private static String toJarUrl(String url) {
        return url.replace("jar://file", "jar:file:");
    }

    private static String getMimeType(String path) {
        String extension = path.substring(path.lastIndexOf("."));
        if (MIME_TYPE_MAP.containsKey(extension)) {
            return MIME_TYPE_MAP.get(extension);
        }
        return "";
    }

    private static final class InterceptJarRequestCallback implements InterceptUrlRequestCallback {

        @Override
        public Response on(Params params) {
            String url = params.urlRequest().url();
            if (!url.startsWith(JAR.name() + ":")) {
                return Response.proceed();
            }
            try {
                DataInputStream inputStream = new DataInputStream(
                        new URL(toJarUrl(url)).openStream());
                byte[] data = new byte[inputStream.available()];
                inputStream.readFully(data);
                inputStream.close();

                String mimeType = getMimeType(params.urlRequest().url());
                UrlRequestJob.Options options = UrlRequestJob.Options
                        .newBuilder(HttpStatus.OK)
                        .addHttpHeader(HttpHeader.of("Content-Type", mimeType))
                        .build();
                UrlRequestJob job = params.newUrlRequestJob(options);
                job.write(data);
                job.complete();
                return Response.intercept(job);
            } catch (IOException e) {
                e.printStackTrace();
                return Response.proceed();
            }
        }
    }
}
