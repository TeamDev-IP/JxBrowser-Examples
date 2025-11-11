# DevTools Protocol ↔ Playwright MCP ↔ LLM

Automates JxBrowser using the [Playwright MCP server][playwright-mcp],
the Chrome DevTools Protocol, and an MCP host like [Cursor][cursor] or
[Claude Desktop][claude].

## Prerequisites

- Java 17+
- Node.js 20+
- A valid [JxBrowser license][licensing]
- An MCP host, such as [Cursor][cursor] or [Claude Desktop][claude].

## Set up MCP server

### Cursor

1. Open Cursor.
2. Go to **Settings...** → **Cursor Settings** → **Tools & MCP**.
3. In the **Installed MCP Servers** tab, click **New MCP Server**.
4. Add the following JSON to your config:

```json
{
  "mcpServers": {
    "playwright": {
      "command": "npx",
      "args": [
        "@playwright/mcp@latest",
        "--cdp-endpoint",
        "http://localhost:9222"
      ]
    }
  }
}
```

http://localhost:9222 must match the debugging URL used by JxBrowser.

5. Save the config and ensure the **playwright** MCP server is enabled.

### Claude Desktop

1. Open Claude Desktop.
2. Go to **Settings...** → **Claude Desktop Settings** → **Developer**.
3. Click the **Edit Config** button to open the configuration file.
4. Add the following JSON to your config:

```json
{
  "mcpServers": {
    "playwright": {
      "command": "npx",
      "args": [
        "@playwright/mcp@latest",
        "--cdp-endpoint",
        "http://localhost:9222"
      ]
    }
  }
}
```

http://localhost:9222 must match the debugging URL used by JxBrowser.

5. Save the configuration file and restart Claude Desktop.

## Run the application

Clone this repository:

```bash
git clone https://github.com/TeamDev-IP/JxBrowser-Examples
```

From the project root, run the app with your license key:

```bash
./gradlew :mcp-devtools:run -Djxbrowser.license.key="<your-license-key>" -Ddebugging.url=http://localhost:9222
```

`-Ddebugging.url` must match the value in the MCP config. If omitted, it
defaults to http://localhost:9222.

This launches a Java window with JxBrowser and enables remote debugging.

## Start automating

Open the AI chat in Cursor or Claude Desktop and try commands like:
> "Open Google and search for TeamDev. Return the company’s phone number."

The MCP host will send requests to the Playwright MCP server, which controls
JxBrowser via the DevTools Protocol.

[playwright-mcp]: https://github.com/microsoft/playwright-mcp
[cursor]: https://cursor.com/
[claude]: https://claude.ai/download
[licensing]: https://teamdev.com/jxbrowser/docs/guides/introduction/licensing/
