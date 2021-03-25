# Eclipse RCP application with embedded JxBrowser

This project is an example Eclipse workspace that demonstrates how to use JxBrowser in Eclipse RCP
projects.

Creating of this example project is described 
in the [tutorial](https://jxbrowser-support.teamdev.com/docs/tutorials/eclipse/rcp-application.html).

## Build and launch
1. Copy necessary JxBrowser JAR files to the workspace:
   
   ```bash
   ./gradlew prepareEclipseExample
   ```
   
2. Open this workspace in Eclipse.
3. In **File -> Import** menu, choose **General -> Existing Projects Into Workspace**.
4. In **Select root directory**, select this directory and import all found projects.
5. Insert your license key into [com.example.e4.rcp/src/com/example/e4/rcp/parts/SamplePart.java]().
6. In **Run** menu, choose **Run Configurations**.
7. Open `com.example.e4.rcp.product` run configuration from **Eclipse Applications** group.
8. In **Plug-ins** tab, click **Add Required Plug-ins** to make sure that all platform-specific SWT components
are present in a classpath.
9. Click **Run**.
