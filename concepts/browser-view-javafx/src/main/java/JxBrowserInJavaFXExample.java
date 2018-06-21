import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserCore;
import com.teamdev.jxbrowser.chromium.internal.Environment;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Demonstrates how to embed Browser instance into JavaFX application.
 */
public class JxBrowserInJavaFXExample extends Application {

    @Override
    public void init() throws Exception {
        // On Mac OS X Chromium engine must be initialized in non-UI thread.
        if (Environment.isMac()) {
            BrowserCore.initialize();
        }
    }

    @Override
    public void start(final Stage primaryStage) {
        Browser browser = new Browser();
        BrowserView view = new BrowserView(browser);

        Scene scene = new Scene(new BorderPane(view), 700, 500);
        primaryStage.setScene(scene);
        primaryStage.show();

        browser.loadURL("http://www.google.com");
    }

    public static void main(String[] args) {
        launch(args);
    }
}