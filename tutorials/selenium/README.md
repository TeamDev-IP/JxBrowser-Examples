# JxBrowser and Selenium WebDriver

This tutorial demonstrates how to create a simple Selenium WebDriver application
that is configured to access a web page in a desktop Java application based on
JxBrowser.

Creating of these example projects is described in
the [tutorial](https://jxbrowser-support.teamdev.com/docs/tutorials/integration/selenium.html).

## Building and Launching

1. In the [launcher](launcher) module, run the `downloadChromeDriver` task 
   to download ChromeDriver to the `resources` directory:
   ```bash
   gradlew downloadChromeDriver
   ```
2. [Insert your license key](https://teamdev.com/jxbrowser/docs/guides/introduction/licensing/#adding-the-license-to-a-project) into [the test application](./target-app/src/main/java/TargetApp.java).
3. In the [target-app](target-app) module, run the `buildApplication` task
   to create an executable file for the current platform that later will be
   run by Selenium WebDriver.
   ```bash
   gradlew clean buildApplication
   ```
4. Run [Selenium](launcher/src/main/java/SeleniumLauncher.java).
5. If everything configured properly, you will see the launched test
   application, and the following in the console output:
   ```
   Current URL: about:blank
   // or
   Current URL: https://www.google.com/ 
   ```
   It means that Selenium WebDriver managed to successfully run the application,
   set up a connection with the JxBrowser's Chromium, and access the loaded
   web page to print its URL.
