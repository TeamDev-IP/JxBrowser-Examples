import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.SearchParams;
import com.teamdev.jxbrowser.chromium.SearchResult;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;

/**
 * This example demonstrates, how to find text on a loaded web page.
 */
public class FindTextExample {
    public static void main(String[] args) {
        final Browser browser = new Browser();
        BrowserView browserView = new BrowserView(browser);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(browserView, BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        browser.addLoadListener(new LoadAdapter() {
            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent event) {
                if (event.isMainFrame()) {
                    SearchParams request = new SearchParams("find me");
                    // Find text from the beginning of the loaded web page.
                    SearchResult result = browser.findText(request);
                    System.out.println(result.indexOfSelectedMatch() + "/" +
                            result.getNumberOfMatches());
                    // Find the same text again from the currently selected match.
                    result = browser.findText(request);
                    System.out.println(result.indexOfSelectedMatch() + "/" +
                            result.getNumberOfMatches());
                }
            }
        });
        browser.loadHTML("<html><body><p>Find me</p><p>Find me</p></body></html>");
    }
}