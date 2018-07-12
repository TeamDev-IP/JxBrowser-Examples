/*
 *  Copyright 2018, TeamDev. All rights reserved.
 *
 *  Redistribution and use in source and/or binary forms, with or without
 *  modification, must retain the above copyright notice and the following
 *  disclaimer.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 *  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import static com.teamdev.jxbrowser.chromium.Browser.invokeAndWaitFinishLoadingMainFrame;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.BrowserType;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import com.teamdev.jxbrowser.chromium.swing.internal.LightWeightWidget;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

/**
 * This example demonstrates how to get screen shot of the web page
 * and save it as PNG image file.
 */
public class HtmlToImage {

    private static final int IMAGE_WIDTH = 1_024;
    private static final int IMAGE_HEIGHT = 1_280;
    private static final int MAX_IMAGE_HEIGHT = 20_000;

    public static void main(String[] args) throws Exception {
        // Disables GPU process and changes maximum texture size
        // value from the default 16384 to the required image height.
        //
        // The maximum texture size value indicates the maximum height
        // of the canvas where Chromium renders web page's content.
        // If the web page's height exceeds the maximum texture size,
        // the part of outsize the texture size will not be drawn and
        // be filled with the black color.
        BrowserPreferences.setChromiumSwitches(
                "--disable-gpu",
                "--max-texture-size=" + MAX_IMAGE_HEIGHT);

        Browser browser = new Browser(BrowserType.LIGHTWEIGHT);
        BrowserView view = new BrowserView(browser);

        // Register LightWeightWidgetListener.onRepaint() to get
        // notifications about paint events. We expect that web page
        // will be completely rendered twice:
        // 1. When its size is updated to the required image width/height.
        // 2. When HTML content is loaded and displayed.
        CountDownLatch latch = new CountDownLatch(2);
        LightWeightWidget widget = (LightWeightWidget) view.getComponent(0);
        widget.addLightWeightWidgetListener((updatedRect, viewSize) -> {
            // Make sure that all view content has been repainted.
            if (viewSize.equals(updatedRect.getSize())) {
                latch.countDown();
            }
        });

        // Set the required view size.
        view.getBrowser().setSize(IMAGE_WIDTH, IMAGE_HEIGHT);

        // Load web page and wait until it's loaded completely.
        invokeAndWaitFinishLoadingMainFrame(view.getBrowser(), targetBrowser ->
                targetBrowser.loadURL("https://www.google.com"));

        // Wait until the web page is rendered.
        latch.await(45, TimeUnit.SECONDS);

        // Get the image of the loaded web page
        Image image = widget.getImage();

        // Save the image of the loaded web page into a PNG file.
        ImageIO.write((RenderedImage) image, "PNG",
                new File("google.com.png"));

        // Dispose the Browser instance.
        browser.dispose();
    }
}
