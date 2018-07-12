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
import com.teamdev.jxbrowser.chromium.ProtocolService;
import com.teamdev.jxbrowser.chromium.URLResponse;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * The example demonstrates a technique of overriding the standard
 * protocol handlers such as HTTP, HTTPS, etc. and registers a custom
 * HTTPS protocol handler that handles all HTTPS requests and
 * "imitates" web server response.
 */
public class HttpsProtocolHandler {

    private static final String HTTPS_PROTOCOL = "https";
    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    private static final String CONTENT_TYPE_HEADER_VALUE = "text/html";

    public static void main(String[] args) {
        Browser browser = new Browser();
        BrowserView view = new BrowserView(browser);

        JFrame frame = new JFrame("JxBrowser – HTTPS Protocol Handler");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(view, BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        BrowserContext browserContext = browser.getContext();
        ProtocolService protocolService = browserContext.getProtocolService();
        protocolService.setProtocolHandler(HTTPS_PROTOCOL, request -> {
            // On every HTTPS request we create and send the following URL response
            // with a simple HTML and the "Content-Type: text/html" header.
            URLResponse response = new URLResponse();
            String html = "<html><body><p>Hello there!</p></body></html>";
            response.setData(html.getBytes());
            response.getHeaders().setHeader(CONTENT_TYPE_HEADER_NAME,
                    CONTENT_TYPE_HEADER_VALUE);
            return response;
        });

        browser.loadURL("https://www.google.com");
    }
}
