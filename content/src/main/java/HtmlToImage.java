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

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.BrowserType;
import com.teamdev.jxbrowser.chromium.Callback;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import com.teamdev.jxbrowser.chromium.swing.internal.LightWeightWidget;
import com.teamdev.jxbrowser.chromium.swing.internal.events.LightWeightWidgetListener;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * This example demonstrates how to get screen shot of the web page
 * and save it as PNG image file.
 */
public class HtmlToImage {
    public static void main(String[] args) throws Exception {
        final int viewWidth = 1024;
        final int viewHeight = 20000;
        // Disables GPU process and changes maximum texture size
        // value from default 16384 to viewHeight. The maximum texture size value
        // indicates the maximum height of the canvas where Chromium
        // renders web page's content. If the web page's height
        // exceeds the maximum texture size, the part of outsize the
        // texture size will not be drawn and will be filled with
        // black color.
        String[] switches = {
                "--disable-gpu",
                "--max-texture-size=" + viewHeight
        };
        BrowserPreferences.setChromiumSwitches(switches);

        // #1 Create LIGHTWEIGHT Browser instance.
        Browser browser = new Browser(BrowserType.LIGHTWEIGHT);
        BrowserView view = new BrowserView(browser);

        // #2 Register LightWeightWidgetListener.onRepaint() to get
        // notifications about paint events. We expect that web page
        // will be completely rendered twice:
        // 1. When its size is updated to viewWidth x viewHeight.
        // 2. When HTML content is loaded and displayed.
        final CountDownLatch latch = new CountDownLatch(2);
        LightWeightWidget widget = (LightWeightWidget) view.getComponent(0);
        widget.addLightWeightWidgetListener(new LightWeightWidgetListener() {
            @Override
            public void onRepaint(Rectangle updatedRect, Dimension viewSize) {
                // Make sure that all view content has been repainted.
                if (viewSize.equals(updatedRect.getSize())) {
                    latch.countDown();
                }
            }
        });

        // #3 Set the required view size.
        browser.setSize(viewWidth, viewHeight);

        // #4 Load web page and wait until web page is loaded completely.
        Browser.invokeAndWaitFinishLoadingMainFrame(browser, new Callback<Browser>() {
            @Override
            public void invoke(Browser browser) {
                browser.loadURL("https://teamdev.com/jxbrowser");
            }
        });

        // #5 Wait until Chromium renders web page content.
        latch.await(45, TimeUnit.SECONDS);

        // #6 Save java.awt.Image of the loaded web page into a PNG file.
        ImageIO.write((RenderedImage) widget.getImage(), "PNG",
                new File("teamdev.com.png"));

        // #7 Dispose Browser instance.
        browser.dispose();
    }
}
