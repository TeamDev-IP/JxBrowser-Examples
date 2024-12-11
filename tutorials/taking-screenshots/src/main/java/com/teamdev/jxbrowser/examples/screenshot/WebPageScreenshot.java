/*
 *  Copyright 2024, TeamDev. All rights reserved.
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

package com.teamdev.jxbrowser.examples.screenshot;

import static com.teamdev.jxbrowser.engine.RenderingMode.OFF_SCREEN;

import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.view.swing.graphics.BitmapImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * This example demonstrates how to take a screenshot of the whole web page including the part that is not visible on the screen.
 */
public final class WebPageScreenshot {

    private static final int PAGE_RENDER_TIMEOUT_MS = 500;

    public static void main(String[] args) throws IOException {
        try (var engine = Engine.newInstance(OFF_SCREEN)) {
            var browser = engine.newBrowser();

            // Load the required web page.
            browser.navigation().loadUrlAndWait("https://html5test.teamdev.com/");

            var frame = browser.mainFrame().orElseThrow();

            // Wait until the web page has been rendered completely.
            Thread.sleep(PAGE_RENDER_TIMEOUT_MS);

            // Get the height of the whole web page,
            // including the invisible part.
            Double pageHeight = frame.executeJavaScript(
                    "Math.max(document.body.scrollHeight, " +
                            "document.documentElement.scrollHeight, document.body.offsetHeight, " +
                            "document.documentElement.offsetHeight, document.body.clientHeight, " +
                            "document.documentElement.clientHeight);");

            // Get the width of the loaded page.
            Double pageWidth = frame.executeJavaScript(
                    "Math.max(document.body.scrollWidth, " +
                            "document.documentElement.scrollWidth, document.body.offsetWidth, " +
                            "document.documentElement.offsetWidth, document.body.clientWidth, " +
                            "document.documentElement.clientWidth);");

            // Resize the browser to the obtained dimensions.
            browser.resize(pageWidth.intValue(), pageHeight.intValue());

            // Obtain the bitmap of the currently loaded web page,
            // including the invisible part.
            var bitmap = browser.bitmap();

            // Convert the bitmap to image.
            var image = BitmapImage.toToolkit(bitmap);

            // Save the image to a PNG file.
            ImageIO.write(image, "PNG", new File("html5test.teamdev.com.png"));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
