BrowserContext browserContext = new BrowserContext(new BrowserContextParams(
    new File("user_data_dir").getAbsolutePath()));
Browser browserOne = new Browser(browserContext);
Browser browserTwo = new Browser(browserContext);