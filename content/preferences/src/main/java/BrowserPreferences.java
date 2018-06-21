Browser browser = new Browser();

// Gets the current Browser's preferences
BrowserPreferences preferences = browser.getPreferences();
preferences.setImagesEnabled(false);
preferences.setJavaScriptEnabled(false);

// Updates Browser's preferences
browser.setPreferences(preferences);

// Images and JavaScript will be disabled
browser.loadURL("http://www.google.com/");
