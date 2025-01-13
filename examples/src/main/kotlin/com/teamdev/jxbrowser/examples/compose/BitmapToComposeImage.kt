/*
 *  Copyright 2025, TeamDev. All rights reserved.
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

package com.teamdev.jxbrowser.examples.compose

import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.navigation
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.view.compose.graphics.toBufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * This example demonstrates how to take a bitmap of the loaded web page,
 * convert it to a Java AWT image, and save to a PNG file.
 */
fun main() {
    val engine = Engine(RenderingMode.OFF_SCREEN)
    val browser = engine.newBrowser().apply { resize(1024, 768) }
    browser.navigation.loadUrlAndWait("https://html5test.teamdev.com/")

    // Get `Bitmap` with the image of the currently loaded web page.
    val bitmap = browser.bitmap()

    // Convert the bitmap to `java.awt.image.BufferedImage`.
    val image = bitmap.toBufferedImage()

    // Save the image to a PNG file.
    ImageIO.write(image, "PNG", File("bitmap.png"))

    // Close the engine and release all resources.
    engine.close()
}
