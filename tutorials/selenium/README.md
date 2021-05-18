# JxBrowser and Selenium WebDriver

This tutorial demonstrates how to create a simple Selenium WebDriver application that is configured
to access a web page loaded in a desktop Java app using JxBrowser on Windows.

Creating of these example projects is described in
the [tutorial](https://jxbrowser-support.teamdev.com/docs/tutorials/integration/selenium.html).

## Building and Launching

1. Download [ChromeDriver](https://sites.google.com/a/chromium.org/chromedriver/downloads). Put it
   into the `launcher/src/main/resources` directory. Use one of the latest versions.
2. [Insert your license key](https://jxbrowser-support.teamdev.com/docs/guides/licensing.html#adding-the-license-to-a-project)
   into [the test application](https://github.com/TeamDev-IP/JxBrowser-Examples/blob/90fdd92f7c4c8737929f57e0383aad39d4be2aee/tutorials/selenium/target-app/src/main/java/TargetApp.java#L45)
   .
3. In the [target-app](target-app) module run the `createExe` Gradle task.
4. Run [Selenium](launcher/src/main/java/SeleniumLauncher.java).

   _You may need to run Selenium twice as during the first start ChromeDriver may not detect the
   running application binaries._

5. If everything configured properly, you will see the launched test application, and the following
   in the console output:
   ```
   Current URL: https://www.google.com/
   ```
   It means that Selenium WebDriver managed to successfully run the application, set up a connection
   with the JxBrowser's Chromium engine, and access the loaded web page to print its URL.
