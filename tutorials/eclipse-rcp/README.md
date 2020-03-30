# `eclipse-rcp`

This project is an example Eclipse workspace that demonstrates how to use JxBrowser in Eclipse RCP
projects.

## Prepare and open workspace
1. Prepare the workspace:
   
   ```bash
   ./gradlew prepareEclipseExample
   ```
   
2. Open this workspace in Eclipse.
3. In `File -> Import` menu choose `General -> Existing Projects Into Workspace`.
4. In `Select root directory`, select this directory and import all found projects.
5. Insert your license key into [BrowserView/src/browserview/parts/JxBrowserView.java]().

### Launch

1. In `BrowserView` project, open `plugin.xml` and then `Overview` tab.
2. Click on `Launch an Eclipse application`. A new Eclipse window should open.
3. In a newly appeared Eclipse window, navigate to `Window -> Show View -> Other` menu.
4. Select `TeamDev -> JxBrowser View`.
