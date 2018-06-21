import com.teamdev.jxbrowser.chromium.*;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;

/**
 * This example demonstrates how to handle all resources such as
 * HTML, PNG, JavaScript, CSS files and decide whether web browser
 * engine should load them from web server or not. For example, in
 * this sample we cancel loading of all Images.
 */
public class ResourceHandlerExample {
    public static void main(String[] args) {
        Browser browser = new Browser();
        BrowserView browserView = new BrowserView(browser);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(browserView, BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        NetworkService networkService = browser.getContext().getNetworkService();
        networkService.setResourceHandler(new ResourceHandler() {
            @Override
            public boolean canLoadResource(ResourceParams params) {
                System.out.println("URL: " + params.getURL());
                System.out.println("Type: " + params.getResourceType());
                boolean isNotImageType =
                        params.getResourceType() != ResourceType.IMAGE;
                if (isNotImageType) {
                    return true;    // Cancel loading of all images
                }
                return false;
            }
        });

        browser.loadURL("http://www.google.com");
    }
}