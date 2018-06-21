import java.awt.Frame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

/**
 * The example demonstrates how to use JxBrowser Swing control in
 * SWT application using SWT_AWT bridge.
 */
public class JxBrowserInSwingSWTExample {
    public static void main(String[] arguments) {
        Browser browser = new Browser();
        BrowserView view = new BrowserView(browser);

        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());

        Composite composite = new Composite(shell,
                SWT.EMBEDDED | SWT.NO_BACKGROUND);
        Frame frame = SWT_AWT.new_Frame(composite);
        frame.add(view);

        browser.loadURL("http://google.com");

        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }
}