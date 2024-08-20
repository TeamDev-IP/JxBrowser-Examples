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

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.singleWindowApplication
import com.google.common.util.concurrent.Uninterruptibles.awaitUninterruptibly
import com.teamdev.jxbrowser.browser.Browser
import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.mainFrame
import com.teamdev.jxbrowser.dsl.browser.navigation
import com.teamdev.jxbrowser.dsl.subscribe
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.navigation.event.FrameLoadFinished
import com.teamdev.jxbrowser.ui.MouseButton
import com.teamdev.jxbrowser.ui.event.MousePressed
import com.teamdev.jxbrowser.ui.event.MouseReleased
import com.teamdev.jxbrowser.ui.event.internal.rpc.mousePressed
import com.teamdev.jxbrowser.ui.event.internal.rpc.mouseReleased
import com.teamdev.jxbrowser.ui.internal.rpc.point
import com.teamdev.jxbrowser.view.compose.BrowserView
import java.util.concurrent.CountDownLatch

/**
 * This example demonstrates how to programmatically dispatch the right click
 * mouse event to the currently loaded web page and get the event notification
 * on JavaScript.
 */
fun main() {
    val engine = Engine(RenderingMode.OFF_SCREEN)
    val browser = engine.newBrowser()

    singleWindowApplication(title = "Dispatch mouse events") {
        Column {
            Button(onClick = { browser.dispatchMouseEvent() }) {
                Text("Dispatch Mouse Right Click")
            }
            BrowserView(browser)
        }
        LaunchedEffect(Unit) {
            browser.loadHtmlAndWait()
        }
    }
}

/**
 * Dispatches [MousePressed] and then [MouseReleased] to simulate a click.
 */
private fun Browser.dispatchMouseEvent() {
    dispatch(
        mousePressed {
            location = point {
                x = 50
                y = 50
            }
            button = MouseButton.SECONDARY
            clickCount = 1
        }
    )
    dispatch(
        mouseReleased {
            location = point {
                x = 50
                y = 50
            }
            button = MouseButton.SECONDARY
            clickCount = 1
        }
    )
}

private fun Browser.loadHtmlAndWait() {
    val latch = CountDownLatch(1)
    navigation.subscribe<FrameLoadFinished> { latch.countDown() }
    mainFrame?.loadHtml(ONCONTEXTMENU_CALLBACK)
    awaitUninterruptibly(latch)
}

/**
 * Assigns `document.oncontextmenu` callback function.
 */
private val ONCONTEXTMENU_CALLBACK = """
    <html lang="en">
    <body>
    <script>
    document.oncontextmenu = function (e) {
        document.body.innerHTML = "DOM event triggered: " + e.type;
        return false;
    }
    </script>
    </body>
    </html>
""".trimIndent()
