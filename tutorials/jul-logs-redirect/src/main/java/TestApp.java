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
import com.teamdev.jxbrowser.logging.Level;
import com.teamdev.jxbrowser.logging.Logger;
import java.util.logging.LogManager;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * This example demonstrates how to capture logs of JxBrowser using SLF4J API.
 *
 * <p>This can be useful when you are using third party logging libraries such as log4j, logback, etc.
 *
 * <p>Since the JxBrowser logging system is built on JUL (java.util.logging) you need to route all incoming log records to
 * the SLF4J API to avoid losing them.
 */
public final class TestApp {

    // The name of the JUL root logger.
    private static final String ROOT_LOGGER_NAME = "com.teamdev.jxbrowser";

    // The logger you use in your application.
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TestApp.class);

    public static void main(String[] args) {

        // Configure JxBrowser logging levels.
        Logger.level(Level.ALL);

        // Configure Log4j.
        BasicConfigurator.configure();

        // Get JUL root logger.
        java.util.logging.Logger julRootLogger = LogManager.getLogManager()
                .getLogger(ROOT_LOGGER_NAME);

        // Remove existing handlers attached to JUL root logger.
        for (java.util.logging.Handler handler : julRootLogger.getHandlers()) {
            julRootLogger.removeHandler(handler);
        }

        // Add SLF4JBridgeHandler to JUL's root logger to route all incoming JUL records to the SLF4j API.
        julRootLogger.addHandler(new SLF4JBridgeHandler());

        logger.info("Application started.");

        // Creating and running Chromium engine.
        Engine engine = Engine.newInstance(HARDWARE_ACCELERATED);
        Browser browser = engine.newBrowser();
        browser.navigation().loadUrl("https://google.com");
        engine.close();

        logger.info("Application finished.");
    }
}
