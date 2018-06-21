BrowserContext context = new BrowserContext(
    new BrowserContextParams(
    new File("my_data_dir").getAbsolutePath()));
Browser browser1 = new Browser(context);
Browser browser2 = new Browser(context);
