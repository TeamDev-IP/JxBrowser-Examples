import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserType;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;

/**
 * The example demonstrates how to use Browser in JInternalFrame components.
 */
public class JInternalFrameExample {
    public static void main(String[] args) {
        JDesktopPane desktopPane = new JDesktopPane();
        desktopPane.add(createInternalFrame("Browser One", "http://www.teamdev.com", 0));
        desktopPane.add(createInternalFrame("Browser Two", "http://www.google.com", 100));

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(desktopPane, BorderLayout.CENTER);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JInternalFrame createInternalFrame(String title, String url, int offset) {
        Browser browser = new Browser(BrowserType.LIGHTWEIGHT);
        BrowserView view = new BrowserView(browser);
        browser.loadURL(url);

        JInternalFrame internalFrame = new JInternalFrame(title, true);
        internalFrame.setContentPane(view);
        internalFrame.setLocation(100 + offset, 100 + offset);
        internalFrame.setSize(400, 400);
        internalFrame.setVisible(true);
        return internalFrame;
    }
}