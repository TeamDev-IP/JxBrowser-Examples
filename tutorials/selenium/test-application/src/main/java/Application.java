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
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Optional;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * This example demonstrates how to integrate a JxBrowser-based application with Selenium
 * WebDriver.
 */
public final class Application {

    private static final String REMOTE_DEBUGGING_PORT_ARG = "--remote-debugging-port=";

    public static void main(String[] args) {
        // Create a builder for EngineOptions.
        EngineOptions.Builder builder = EngineOptions.newBuilder(HARDWARE_ACCELERATED);

        // Pass the remote debugging port from the command line if it presents.
        remoteDebuggingPortFromCommandLine(args).ifPresent(builder::remoteDebuggingPort);

        // Creating Chromium engine.
        Engine engine = Engine.newInstance(builder.build());
        Browser browser = engine.newBrowser();

        SwingUtilities.invokeLater(() -> {
            // Creating Swing component for rendering web content
            // loaded in the given Browser instance.
            BrowserView view = BrowserView.newInstance(browser);

            // Creating and displaying Swing app frame.
            JFrame frame = new JFrame();

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    engine.close();
                }
            });
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.add(view, BorderLayout.CENTER);
            frame.setSize(800, 700);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            browser.navigation().loadUrl("https://google.com");
        });
    }

    private static Optional<Integer> remoteDebuggingPortFromCommandLine(String[] args) {
        if (args.length > 0) {
            for (String arg : args) {
                if (arg.startsWith(REMOTE_DEBUGGING_PORT_ARG)) {
                    String port = arg.substring(REMOTE_DEBUGGING_PORT_ARG.length());
                    return Optional.of(Integer.parseInt(port));
                }
            }
        }
        return Optional.empty();
    }
}
