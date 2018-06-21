BrowserContext context1 = new BrowserContext(
    new BrowserContextParams(
    new File("my_data_dir").getAbsolutePath()));
Browser browser1 = new Browser(context1);

BrowserContext context2 = new BrowserContext(
    new BrowserContextParams(
    new File("my_data_dir").getAbsolutePath()));
Browser browser2 = new Browser(context2);
