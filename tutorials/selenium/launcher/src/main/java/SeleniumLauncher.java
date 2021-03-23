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

import java.io.File;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * An application that configures Selenium WebDriver (ChromeDriver) to run on the JxBrowser-based
 * application binaries and get access to HTML content loaded in JxBrowser.
 */
public final class SeleniumLauncher {

    public static void main(String[] args) {
        // Set a path to the ChromeDriver executable.
        System.setProperty("webdriver.chrome.driver",
                "tutorials/selenium/launcher/src/main/resources/chromedriver.exe");

        ChromeOptions options = new ChromeOptions();

        // Set a path to your JxBrowser application executable.
        options.setBinary(
                new File("tutorials/selenium/target-app/build/executable/TargetApp.exe"));
        // Set a port to communicate on.
        options.addArguments("--remote-debugging-port=9222");

        WebDriver driver = new ChromeDriver(options);

        // Now you can use WebDriver.
        System.out.printf("Current URL: %s\n", driver.getCurrentUrl());

        driver.quit();
    }
}
