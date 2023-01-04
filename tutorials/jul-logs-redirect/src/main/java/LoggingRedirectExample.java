/*
 *  Copyright 2023, TeamDev. All rights reserved.
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
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * This example demonstrates how to capture JxBrowser logs using SLF4J API.
 */
public final class LoggingRedirectExample {

    public static void main(String[] args) {

        // Configure JxBrowser logging level.
        Logger.level(Level.ALL);

        // Get the JxBrowser logger.
        java.util.logging.Logger jxBrowserLogger =
                LogManager.getLogManager().getLogger("com.teamdev.jxbrowser");

        // Remove existing handlers attached to the JxBrowser logger.
        for (java.util.logging.Handler handler : jxBrowserLogger.getHandlers()) {
            jxBrowserLogger.removeHandler(handler);
        }

        // Add SLF4JBridgeHandler to the JxBrowser logger to route log records to the SLF4j.
        jxBrowserLogger.addHandler(new SLF4JBridgeHandler());

        // Creating and running Chromium engine.
        Engine engine = Engine.newInstance(HARDWARE_ACCELERATED);
        Browser browser = engine.newBrowser();
        browser.navigation().loadUrl("https://google.com");
        engine.close();
    }
}
