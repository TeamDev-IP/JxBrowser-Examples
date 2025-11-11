# Chrome Extension ↔ Browser MCP ↔ LLM

Automates JxBrowser using the [Browser MCP][browsermcp] Chrome extension
and [Cursor][cursor].

## Prerequisites

- Java 17+
- Node.js 20+
- A valid [JxBrowser license][licensing]
- [Cursor][cursor] — a code editor that supports MCP

## Set up MCP server

1. Open Cursor.
2. Go to **Settings...** → **Cursor Settings** → **Tools & MCP**.
3. In the **Installed MCP Servers** tab, click **New MCP Server**.
4. Add the following JSON to your config:

```json
{
  "mcpServers": {
    "browsermcp": {
      "command": "npx",
      "args": [
        "@browsermcp/mcp"
      ]
    }
  }
}
```

5. Save the config and ensure the **browsermcp** MCP server is enabled.

## Run the application

Clone this repository:

```bash
git clone https://github.com/TeamDev-IP/JxBrowser-Examples
```

From the project root, run the app with your license key:

```bash
./gradlew :mcp-extension:run -Djxbrowser.license.key="<your-license-key>"
```

This launches a Java window with JxBrowser and loads the Browser MCP Chrome
extension.

## Start automating

Open the AI chat in Cursor and try commands like:
> "Open Google and search for TeamDev. Return the company’s phone number."

Cursor will send requests to the Browser MCP server, which controls JxBrowser
via the installed Browser MCP Chrome extension.

[browsermcp]: https://browsermcp.io/
[cursor]: https://cursor.com/
[licensing]: https://teamdev.com/jxbrowser/docs/guides/introduction/licensing/
