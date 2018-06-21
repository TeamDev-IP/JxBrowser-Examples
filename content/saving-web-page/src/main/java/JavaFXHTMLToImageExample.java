import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.BrowserType;
import com.teamdev.jxbrowser.chromium.Callback;
import com.teamdev.jxbrowser.chromium.internal.LightWeightWidgetListener;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import com.teamdev.jxbrowser.chromium.javafx.internal.LightWeightWidget;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 * The example demonstrates how to get screen shot of the web page
 * and save it as PNG image file.
 */
public class JavaFXHTMLToImageExample extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final int viewWidth = 1024;
        final int viewHeight = 15000;
        // Disables GPU process and changes maximum texture size
        // value from default 16384 to viewHeight. The maximum texture size value
        // indicates the maximum height of the canvas where Chromium
        // renders web page's content. If the web page's height
        // exceeds the maximum texture size, the part of outsize the
        // texture size will not be drawn and will be filled with
        // black color.
        String[] switches = {
                "--disable-gpu",
                "--disable-gpu-compositing",
                "--enable-begin-frame-scheduling",
                "--max-texture-size=" + viewHeight
        };
        BrowserPreferences.setChromiumSwitches(switches);

        // #1 Create LIGHTWEIGHT Browser instance
        final Browser browser = new Browser(BrowserType.LIGHTWEIGHT);
        final BrowserView view = new BrowserView(browser);

        // #2 Get javafx.scene.image.Image of the loaded web page
        final LightWeightWidget widget = (LightWeightWidget) view.getChildren().get(0);

        // #3 Register LightWeightWidgetListener.onRepaint() to get
        // notifications about paint events. We expect that web page
        // will be completely rendered twice:
        // 1. When its size is updated to viewWidth x viewHeight.
        // 2. When HTML content is loaded and displayed.
        final CountDownLatch latch = new CountDownLatch(2);
        widget.addLightWeightWidgetListener(new LightWeightWidgetListener() {
            @Override
            public void onRepaint(Rectangle updatedRect, Dimension viewSize) {
                if (viewSize.equals(updatedRect.getSize())) {
                    latch.countDown();
                }
            }
        });

        // #4 Set the required view size
        browser.setSize(viewWidth, viewHeight);

        // #5 Load web page and wait until web page is loaded completely
        Browser.invokeAndWaitFinishLoadingMainFrame(browser, new Callback<Browser>() {
            @Override
            public void invoke(Browser browser) {
                browser.loadURL("https://teamdev.com/jxbrowser");
            }
        });

        // #6 Wait until Chromium renders web page content.
        latch.await(45, TimeUnit.SECONDS);

        final Image image = widget.getImage();

        // #7 Save the image into a PNG file
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "PNG", new File("teamdev.com.png"));

        // #8 Dispose Browser instance.
        browser.dispose();

        // #9 Close the application.
        Platform.exit();
    }
}