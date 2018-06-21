import com.teamdev.jxbrowser.chromium.*;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.InputStream;
import java.net.URL;

public class ProtocolHandlerExample {
    public static void main(String[] args) {
        final Browser browser = new Browser();
        BrowserView view = new BrowserView(browser);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(view, BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        BrowserContext browserContext = browser.getContext();
        ProtocolService protocolService = browserContext.getProtocolService();
        protocolService.setProtocolHandler("jar", new ProtocolHandler() {
            @Override
            public URLResponse onRequest(URLRequest request) {
                try {
                    URLResponse response = new URLResponse();
                    URL path = new URL(request.getURL());
                    InputStream inputStream = path.openStream();
                    DataInputStream stream = new DataInputStream(inputStream);
                    byte[] data = new byte[stream.available()];
                    stream.readFully(data);
                    response.setData(data);
                    String mimeType = getMimeType(path.toString());
                    response.getHeaders().setHeader("Content-Type", mimeType);
                    return response;
                } catch (Exception ignored) {}
                return null;
            }
        });

        // Assume that we need to load a resource related to this class in the JAR file
        browser.loadURL(ProtocolHandlerExample.class.getResource("index.html").toString());
    }

    private static String getMimeType(String path) {
        if (path.endsWith(".html")) {
            return "text/html";
        }
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html";
    }
}
