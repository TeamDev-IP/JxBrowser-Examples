/*
 *  Copyright 2022, TeamDev. All rights reserved.
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
import com.teamdev.jxbrowser.frame.Frame;
import com.teamdev.jxbrowser.logging.Level;
import com.teamdev.jxbrowser.logging.Logger;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.LogManager;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * This example demonstrates how to enable JxBrowser logging using SLF4J API. This can be useful
 * when you are using third party logging libraries such as log4j, logback, etc. Since the JxBrowser
 * logging system is built on JUL (java.util.logging) you need to route all incoming log records to
 * the SLF4J API to avoid losing them.
 *
 * <p>
 * Test application represents the simple Swing component for rendering web content.
 */
public final class TestApp {

    // The name of the j.u.l root logger that the JxBrowser uses.
    private static final String ROOT_LOGGER_NAME = "com.teamdev.jxbrowser";

    // Logger you use in your application.
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TestApp.class);

    public static void main(String[] args) {

        // Configure JxBrowser logging levels.
        Logger.level(Level.ALL);

        // Configure log4j.
        BasicConfigurator.configure();

        // Get j.u.l root logger.
        java.util.logging.Logger julRootLogger = LogManager.getLogManager()
                .getLogger(ROOT_LOGGER_NAME);

        // Remove existing handlers attached to j.u.l root logger.
        for (java.util.logging.Handler handler : julRootLogger.getHandlers()) {
            julRootLogger.removeHandler(handler);
        }

        // Add SLF4JBridgeHandler to j.u.l's root logger to route all incoming j.u.l records to the SLF4j API.
        julRootLogger.addHandler(new SLF4JBridgeHandler());

        logger.info("Application started.");

        // Creating and running Chromium engine.
        Engine engine = Engine.newInstance(HARDWARE_ACCELERATED);
        Browser browser = engine.newBrowser();

        SwingUtilities.invokeLater(() -> {
            // Creating Swing component for rendering web content
            // loaded in the given Browser instance.
            final BrowserView view = BrowserView.newInstance(browser);

            // Creating and displaying Swing app frame.
            JFrame frame = new JFrame("Hello World");

            // Close Engine and onClose app window.
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    engine.close();
                    logger.debug("Window closed.");
                }
            });
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            JTextField addressBar = new JTextField("https://google.com");
            addressBar.addActionListener(e -> browser.navigation()
                    .loadUrl(addressBar.getText()));
            frame.add(addressBar, BorderLayout.NORTH);
            frame.add(view, BorderLayout.CENTER);
            JButton print = new JButton("Print");
            print.addActionListener(e -> browser.mainFrame().ifPresent(Frame::print));
            frame.add(print, BorderLayout.SOUTH);
            frame.setSize(1280, 900);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            browser.navigation().loadUrl(addressBar.getText());
        });
    }
}
