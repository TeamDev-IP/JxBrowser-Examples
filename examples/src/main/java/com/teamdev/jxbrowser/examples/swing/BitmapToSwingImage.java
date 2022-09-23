/*
 *  Copyright 2022, TeamDev. All rights reserved.
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

package com.teamdev.jxbrowser.examples.swing;

import static com.teamdev.jxbrowser.engine.RenderingMode.OFF_SCREEN;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.ui.Bitmap;
import com.teamdev.jxbrowser.view.swing.graphics.BitmapImage;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * This example demonstrates how to take bitmap of the loaded web page, convert it to a Java AWT
 * image and save it to a PNG file.
 */
public final class BitmapToSwingImage {

    public static void main(String[] args) throws IOException {
        try (Engine engine = Engine.newInstance(OFF_SCREEN)) {
            Browser browser = engine.newBrowser();

            // Resize browser to the required dimension
            browser.resize(1024, 768);

            // Load the required web page and wait until it is loaded completely
            browser.navigation().loadUrlAndWait("https://www.google.com");

            Bitmap bitmap = browser.bitmap();

            // Convert the bitmap to java.awt.image.BufferedImage
            BufferedImage bufferedImage = BitmapImage.toToolkit(bitmap);

            // Save the image to a PNG file
            ImageIO.write(bufferedImage, "PNG", new File("bitmap.png"));
        }
    }
}
