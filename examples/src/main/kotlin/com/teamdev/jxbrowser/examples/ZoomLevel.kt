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

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.singleWindowApplication
import com.teamdev.jxbrowser.compose.BrowserView
import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.navigation
import com.teamdev.jxbrowser.dsl.browser.zoom
import com.teamdev.jxbrowser.dsl.subscribe
import com.teamdev.jxbrowser.dsl.zoomLevels
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.navigation.event.FrameLoadFinished
import com.teamdev.jxbrowser.zoom.ZoomLevel
import com.teamdev.jxbrowser.zoom.event.ZoomLevelChanged

/**
 * This example demonstrates how to modify zoom level for a currently
 * loaded web page.
 *
 * Zoom level will be applied to the currently loaded page only.
 *
 * If you navigate to a different domain, its zoom level will be 100%
 * until you modify it.
 */
fun main() {
    val engine = Engine(RenderingMode.HARDWARE_ACCELERATED)
    val browser = engine.newBrowser()

    // Listen to the zoom changes.
    engine.zoomLevels.subscribe<ZoomLevelChanged> { event ->
        event.print()
    }

    // Change zoom of the currently loaded page.
    browser.navigation.subscribe<FrameLoadFinished> { event ->
        if (event.frame().isMain) {
            browser.zoom.level(ZoomLevel.P_200)
        }
    }

    singleWindowApplication(title = "Change Zoom Level") {
        BrowserView(browser)
        LaunchedEffect(Unit) {
            browser.navigation.loadUrl("https://www.google.com")
        }
    }
}

private fun ZoomLevelChanged.print() = println(
    """
    Url: ${host()}
    Zoom level: ${level()}
""".trimIndent()
)
