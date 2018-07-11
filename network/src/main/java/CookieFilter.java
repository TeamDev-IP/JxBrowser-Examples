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
import com.teamdev.jxbrowser.chromium.Cookie;
import com.teamdev.jxbrowser.chromium.NetworkService;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import com.teamdev.jxbrowser.chromium.swing.DefaultNetworkDelegate;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * The example demonstrates how to suppress/filter all
 * the incoming and outgoing cookies.
 */
public class CookieFilter {

    public static void main(String[] args) {
        Browser browser = new Browser();
        BrowserView browserView = new BrowserView(browser);

        // Suppress/filter all the incoming and outgoing cookies.
        BrowserContext browserContext = browser.getContext();
        NetworkService networkService = browserContext.getNetworkService();
        networkService.setNetworkDelegate(new DefaultNetworkDelegate() {
            @Override
            public boolean onCanSetCookies(String url, List<Cookie> cookies) {
                System.out.println("Disallow accepting cookies for: " + url);
                // Return false to disallow accepting and saving the cookies.
                return false;
            }

            @Override
            public boolean onCanGetCookies(String url, List<Cookie> cookies) {
                System.out.println("Disallow sending cookies for: " + url);
                // Return false to disallow sending the cookies to a web server.
                return false;
            }
        });

        JFrame frame = new JFrame("JxBrowser – Filter Cookies");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(browserView, BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        browser.loadURL("https://www.google.com");
    }
}
