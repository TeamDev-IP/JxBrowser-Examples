import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;

/**
 * The example demonstrates how to use Browser in JTabbedPane.
 */
public class TabbedPaneExample {
    public static void main(String[] args) {
        Browser browserOne = new Browser();
        Browser browserTwo = new Browser();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Browser One", new BrowserView(browserOne));
        tabbedPane.addTab("Browser Two", new BrowserView(browserTwo));

        JFrame frame = new JFrame();
        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        browserOne.loadURL("http://www.google.com");
        browserTwo.loadURL("http://www.teamdev.com");
    }
}