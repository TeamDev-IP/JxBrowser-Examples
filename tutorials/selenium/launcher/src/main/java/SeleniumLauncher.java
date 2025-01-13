/*
 *  Copyright 2025, TeamDev. All rights reserved.
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

import static com.teamdev.jxbrowser.os.Environment.isLinux;
import static com.teamdev.jxbrowser.os.Environment.isMac;
import static com.teamdev.jxbrowser.os.Environment.isWindows;
import static java.util.Objects.requireNonNull;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Configures Selenium WebDriver (ChromeDriver) to run the JxBrowser-based
 * application and get access to the loaded web page.
 */
public final class SeleniumLauncher {

    public static void main(String[] args) throws URISyntaxException {
        URL chromeDriverUrl = requireNonNull(
                SeleniumLauncher.class.getResource(chromeDriverFile()));
        // Set a path to the ChromeDriver executable.
        System.setProperty("webdriver.chrome.driver",
                Paths.get(chromeDriverUrl.toURI()).toString());

        // #docfragment "path-to-exe"
        var options = new ChromeOptions();

        // Set a path to your JxBrowser application executable.
        options.setBinary(new File(binaryPath()));
        // #enddocfragment "path-to-exe"
        // #docfragment "set-remote-debugging-port"
        // Set a port to communicate on.
        options.addArguments("--remote-debugging-port=9222");
        // #enddocfragment "set-remote-debugging-port"

        var driver = new ChromeDriver(options);

        // Now you can use WebDriver.
        System.out.printf("Current URL: %s\n", driver.getCurrentUrl());

        driver.quit();
    }

    private static String binaryPath() {
        var applicationDirectory =
                "tutorials/selenium/target-app/build/application/";
        if (isMac()) {
            return applicationDirectory
                    + "App.app/Contents/MacOS/App";
        } else if (isWindows()) {
            return applicationDirectory + "App/App.exe";
        } else if (isLinux()) {
            return applicationDirectory + "App/bin/App";
        }

        throw new IllegalStateException("The platform is unsupported.");
    }

    private static String chromeDriverFile() {
        if (isWindows()) {
            return "chromedriver.exe";
        }
        return "chromedriver";
    }
}
