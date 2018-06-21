@Override
public void init() throws Exception {
    // On macOS platform the Chromium engine must be initialized in non-UI thread.
    if (Environment.isMac()) {
        BrowserCore.initialize();
    }
}