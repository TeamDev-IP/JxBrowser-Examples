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

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.navigation.event.LoadFinished;
import com.teamdev.jxbrowser.view.swing.BrowserView;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Simple Swing application that works in Docker.
 *
 * <p>Prints the web page's title once the page is loaded.
 */
public final class DemoApp {

    public static void main(String[] args) {
        // Set your JxBrowser license key.
        System.setProperty("jxbrowser.license.key", "ENTER YOUR LICENSE KEY HERE");

        EngineOptions.Builder options = EngineOptions.newBuilder(HARDWARE_ACCELERATED);

        // #docfragment "disable-gpu"
        // Disable GPU accelerated rendering.
        // This option is required as the GPU is not available in the Docker container.
        options.disableGpu();
        // #enddocfragment "disable-gpu"

        // #docfragment "enable-remote-debugging-port"
        // Enable the remote debugging port.
        options.remoteDebuggingPort(9222);
        // #enddocfragment "enable-remote-debugging-port"

        // Creating Chromium engine.
        Engine engine = Engine.newInstance(options.build());
        Browser browser = engine.newBrowser();

        // Print the web page's title once the page is loaded.
        browser.navigation().on(LoadFinished.class, event -> {
            System.out.println("Title: " + browser.title());
        });

        SwingUtilities.invokeLater(() -> {
            final BrowserView view = BrowserView.newInstance(browser);
            JFrame frame = new JFrame("Demo App");
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    engine.close();
                }
            });
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.add(view, BorderLayout.CENTER);
            frame.setSize(800, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            browser.navigation().loadUrl("https://google.com");
        });
    }
}
