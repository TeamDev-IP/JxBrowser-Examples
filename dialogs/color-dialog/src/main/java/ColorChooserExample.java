import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.CloseStatus;
import com.teamdev.jxbrowser.chromium.ColorChooserParams;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import com.teamdev.jxbrowser.chromium.swing.DefaultDialogHandler;

import javax.swing.*;
import java.awt.*;

/**
 * This example demonstrates how to override behaviour of standard
 * color chooser dialog for HTML5 input color element.
 */
public class ColorChooserExample {
    public static void main(String[] args) {
        Browser browser = new Browser();
        BrowserView view = new BrowserView(browser);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(view, BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        browser.setDialogHandler(new DefaultDialogHandler(view) {
            @Override
            public CloseStatus onColorChooser(ColorChooserParams params) {
                params.setColor(Color.BLUE);
                return CloseStatus.OK;
            }
        });

        browser.loadHTML("<html><body><input type='color' value='#ff000'></body></html>");
    }
}
