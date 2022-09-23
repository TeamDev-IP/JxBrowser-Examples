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

package com.teamdev.jxbrowser.examples.swt;

import static com.teamdev.jxbrowser.engine.RenderingMode.OFF_SCREEN;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.ui.Bitmap;
import com.teamdev.jxbrowser.view.swt.graphics.BitmapImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

/**
 * This example demonstrates how to take bitmap of the loaded web page, convert it to an SWT image
 * and save it to a PNG file.
 */
public final class BitmapToSwtImage {

    public static void main(String[] args) {
        Display display = new Display();
        try (Engine engine = Engine.newInstance(OFF_SCREEN)) {
            Browser browser = engine.newBrowser();

            // Resize browser to the required dimension
            browser.resize(1024, 768);

            // Load the required web page and wait until it is loaded completely
            browser.navigation().loadUrlAndWait("https://www.google.com");

            Bitmap bitmap = browser.bitmap();

            // Convert the bitmap to org.eclipse.swt.graphics.Image
            Image image = BitmapImage.toToolkit(display, bitmap);

            // Save the image to a PNG file
            ImageLoader loader = new ImageLoader();
            loader.data = new ImageData[]{image.getImageData()};
            loader.save("bitmap.png", SWT.IMAGE_PNG);
        }
        display.dispose();
    }
}
