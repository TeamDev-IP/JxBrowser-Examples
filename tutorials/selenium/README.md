# JxBrowser and Selenium WebDriver

This tutorial demonstrates how to create a simple Selenium WebDriver application
that is configured to access a web page in a desktop Java application based on
JxBrowser.

Creating of these example projects is described in
the [tutorial](https://jxbrowser-support.teamdev.com/docs/tutorials/integration/selenium.html).

## Prerequisites

The tutorial requires the `jpackage` tool available in `PATH`, so make sure that
the environment variable contains the `%JAVA_HOME%/bin` directory.

## Building and Launching

1. In the [root](../..) directory, run the `downloadChromeDriver` task of
   the [launcher](launcher) module to download ChromeDriver to the
   `resources` directory:
   
   **Windows:**
   ```bash
   gradlew launcher:downloadChromeDriver
   ```
   
   **macOS/Linux:**
     ```bash
   ./gradlew launcher:downloadChromeDriver
   ```

   Each version of ChromeDriver corresponds to the Chromium version used in
   JxBrowser. If you change the JxBrowser version
   in [build.gradle.kts](../../build.gradle.kts) you need to update the
   `chromiumVersion` property [here](launcher/build.gradle.kts)
   before running the `downloadChromeDriver` task. Otherwise, Selenium WebDriver
   will not be able to connect to Chromium. You can find which Chromium version
   is used in the specific JxBrowser version in
   the [release notes](https://teamdev.com/jxbrowser/release-notes/).
2. [Insert your license key](https://teamdev.com/jxbrowser/docs/guides/introduction/licensing/#adding-the-license-to-a-project)
   into [the test application](./target-app/src/main/java/App.java).
3. In the [root](../..) directory, run the `buildApplication` task
   of the [target-app](target-app) module to create an executable file for the
   current platform that later will be run by Selenium WebDriver.

   **Windows:**
   ```bash
   gradlew target-app:clean target-app:buildApplication
   ```
   
   **macOS/Linux:**
   ```bash
   ./gradlew target-app:clean target-app:buildApplication
   ```
   
4. Run [Selenium](launcher/src/main/java/SeleniumLauncher.java).
5. If everything configured properly, you will see the launched test
   application, and the following console output:
   ```
   Current URL: about:blank
   // or
   Current URL: https://www.google.com/ 
   ```
   It means that Selenium WebDriver managed to successfully run the application,
   set up a connection with the JxBrowser's Chromium, and access the loaded
   web page to print its URL.
