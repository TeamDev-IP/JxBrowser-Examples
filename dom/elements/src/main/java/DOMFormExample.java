import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.dom.By;
import com.teamdev.jxbrowser.chromium.dom.DOMDocument;
import com.teamdev.jxbrowser.chromium.dom.DOMElement;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;

/**
 * The example demonstrates how to fill HTML Form fields using JxBrowser DOM API.
 */
public class DOMFormExample {
    public static void main(String[] args) {
        Browser browser = new Browser();
        BrowserView browserView = new BrowserView(browser);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(browserView, BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        browser.addLoadListener(new LoadAdapter() {
            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent event) {
                if (event.isMainFrame()) {
                    Browser browser = event.getBrowser();
                    DOMDocument document = browser.getDocument();
                    DOMElement firstName = document.findElement(By.name("firstName"));
                    firstName.setAttribute("value", "John");
                    DOMElement lastName = document.findElement(By.name("lastName"));
                    lastName.setAttribute("value", "Doe");
                }
            }
        });
        browser.loadHTML("<html><body><form name=\"myForm\">" +
                "First name: <input type=\"text\" name=\"firstName\"/><br/>" +
                "Last name: <input type=\"text\" name=\"lastName\"/><br/>" +
                "<input type=\"button\" value=\"Save\"/>" +
                "</form></body></html>");
    }
}
