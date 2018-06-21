import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;

/**
 * The example demonstrates how to use JxBrowser JavaFX
 * control in SWT application using FXCanvas.
 */
public class JxBrowserInJavaFXSWTExample {
    public static void main(String[] arguments) {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());

        Browser browser = new Browser();
        FXCanvas canvas = new FXCanvas(shell, SWT.NONE);

        // BrowserView instance must be initialized after FXCanvas.
        BrowserView view = new BrowserView(browser);
        canvas.setScene(new Scene(view));

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
