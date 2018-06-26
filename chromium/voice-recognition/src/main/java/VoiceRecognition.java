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
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * The example demonstrates how to enable voice recognition functionality in Chromium
 * engine. By default, voice recognition functionality is disabled. To enable it you must
 * provide your Google API Keys to the Chromium engine as shown in this example.
 *
 * The instruction that describes how to acquire the API keys you can find at
 * https://www.chromium.org/developers/how-tos/api-keys
 */
public class VoiceRecognition {

    private static final String GOOGLE_API_KEY = "GOOGLE_API_KEY";
    private static final String GOOGLE_DEFAULT_CLIENT_ID = "GOOGLE_DEFAULT_CLIENT_ID";
    private static final String GOOGLE_DEFAULT_CLIENT_SECRET = "GOOGLE_DEFAULT_CLIENT_SECRET";

    public static void main(String[] args) {
        // It's very important that you configure the API keys
        // before you create the first Browser instance.
        BrowserPreferences.setChromiumVariable(GOOGLE_API_KEY, "your_api_key");
        BrowserPreferences.setChromiumVariable(GOOGLE_DEFAULT_CLIENT_ID, "your_client_id");
        BrowserPreferences.setChromiumVariable(GOOGLE_DEFAULT_CLIENT_SECRET, "your_client_secret");

        Browser browser = new Browser();
        BrowserView view = new BrowserView(browser);

        JFrame frame = new JFrame("JxBrowser Examples: Voice Recognition");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(view, BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // To test the voice recognition click the "Search by voice" icon in the search field.
        browser.loadURL("https://google.com");
    }
}
