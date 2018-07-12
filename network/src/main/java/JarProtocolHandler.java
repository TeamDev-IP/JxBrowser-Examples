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

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.ProtocolHandler;
import com.teamdev.jxbrowser.chromium.ProtocolService;
import com.teamdev.jxbrowser.chromium.URLRequest;
import com.teamdev.jxbrowser.chromium.URLResponse;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import java.awt.BorderLayout;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * The example demonstrates how to register a JAR protocol handler to load
 * the files located inside a JAR archive.
 */
public class JarProtocolHandler {

    private static final String JAR_PROTOCOL = "jar";

    private static final String EXTENSION_HTML = ".html";
    private static final String EXTENSION_CSS = ".css";
    private static final String EXTENSION_JS = ".js";

    private static final String CONTENT_TYPE = "Content-Type";

    private static final String MIME_TYPE_TEXT_HTML = "text/html";
    private static final String MIME_TYPE_TEXT_CSS = "text/css";
    private static final String MIME_TYPE_TEXT_JS = "text/javascript";

    private static final String ERROR_PAGE_HTML = "<html><title>Error Page</title>"
            + "<body>Oops! Something went wrong:</br>Error message: %s</body></html>";

    public static void main(String[] args) {
        // Create and initialize a new BrowserView instance.
        BrowserView view = new BrowserView();

        // Create and display a window with the BrowserView component.
        JFrame frame = new JFrame("JxBrowser – JAR Protocol Handler");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(view, BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Register a custom ProtocolHandler for the "jar" protocol.
        Browser browser = view.getBrowser();
        BrowserContext browserContext = browser.getContext();
        ProtocolService protocolService = browserContext.getProtocolService();
        protocolService.setProtocolHandler(JAR_PROTOCOL, new ProtocolHandler() {
            @Override
            public URLResponse onRequest(URLRequest request) {
                URLResponse response = new URLResponse();
                try {
                    // Find the requested file by URL.
                    URL url = new URL(request.getURL());
                    System.out.println("url = " + url);
                    // Read the file's bytes.
                    DataInputStream dataInputStream = new DataInputStream(url.openStream());
                    byte[] data = new byte[dataInputStream.available()];
                    dataInputStream.readFully(data);
                    dataInputStream.close();
                    // Put the file's bytes in the URLResponse.
                    response.setData(data);
                    // Detect the MIME type of the requested file and put it to
                    // the "Content-Type" header of the URLResponse.
                    response.getHeaders().setHeader(CONTENT_TYPE, getMIMEType(url));
                } catch (MalformedURLException e) {
                    sendErrorPage(response, "Failed to parse URL: " + request.getURL());
                } catch (IOException e) {
                    sendErrorPage(response, "Failed to read file: " + e.getMessage());
                } catch (Exception e) {
                    sendErrorPage(response, e.getMessage());
                }
                return response;
            }

            private void sendErrorPage(URLResponse response, String errorMessage) {
                // Send an error page with the given error message.
                response.setData(String.format(ERROR_PAGE_HTML, errorMessage).getBytes());
                response.getHeaders().setHeader(CONTENT_TYPE, MIME_TYPE_TEXT_HTML);
            }

            private String getMIMEType(URL url) {
                // Return "text/html" for the ".html" file extensions.
                if (url.toString().endsWith(EXTENSION_HTML)) {
                    return MIME_TYPE_TEXT_HTML;
                }
                // Return "text/css" for the ".css" file extensions.
                if (url.toString().endsWith(EXTENSION_CSS)) {
                    return MIME_TYPE_TEXT_CSS;
                }
                // Return "text/javascript" for the ".js" file extensions.
                if (url.toString().endsWith(EXTENSION_JS)) {
                    return MIME_TYPE_TEXT_JS;
                }
                // Throw an exception in case of unsupported file extension.
                throw new IllegalArgumentException("Unsupported file format: " + url);
            }
        });

        // Load the index.html file located inside a JAR archive added to this Java app classpath.
        browser.loadURL(JarProtocolHandler.class.getResource("index.html").toString());
    }
}
