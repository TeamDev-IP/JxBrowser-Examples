# JxBrowser and Selenium WebDriver

These projects demonstrate how to integrate a JxBrowser-based application with Selenium WebDriver.

Creating of these example projects is described in the [tutorial](https://jxbrowser-support.teamdev.com/docs/tutorials/integration/selenium.html). 

## Build and launch

1. [Insert your license key](https://jxbrowser-support.teamdev.com/docs/guides/licensing.html#adding-the-license-to-a-project) into [the test application](test-application/src/main/java/Application.java).
2. In a [test-application](test-application) module run `createExe` gradle task.
3. Run [Selenium](selenium-starter/src/main/java/SeleniumConfigurationExample.java).

   _You may need to run Selenium twice as during the first start ChromeDriver may not detect the running application binaries._
   