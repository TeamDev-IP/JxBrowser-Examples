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

package com.teamdev.jxbrowser.examples

import com.teamdev.jxbrowser.browser.SavePageType
import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.navigation
import com.teamdev.jxbrowser.engine.RenderingMode
import kotlin.io.path.Path
import kotlin.io.path.absolute

/**
 * This example demonstrates how to save the loaded web page.
 */
fun main() {
    val engine = Engine(RenderingMode.HARDWARE_ACCELERATED)
    val browser = engine.newBrowser()

    browser.navigation.loadUrlAndWait("https://www.google.com")

    val html = Path("index.html")
    val resources = Path("resources_dir")

    browser.saveWebPage(html, resources, SavePageType.COMPLETE_HTML)
        .also { success ->
            if (success) {
                println("The web page saved to `${html.absolute()}`.")
                println("Page resources saved to `${resources.absolute()}`.")
            } else {
                println("Failed to save the web page to $html")
            }
        }

    engine.close()
}
