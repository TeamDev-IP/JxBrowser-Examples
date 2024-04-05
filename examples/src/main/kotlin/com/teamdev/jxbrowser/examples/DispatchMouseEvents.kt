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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.singleWindowApplication
import com.google.common.util.concurrent.Uninterruptibles.awaitUninterruptibly
import com.teamdev.jxbrowser.browser.Browser
import com.teamdev.jxbrowser.compose.BrowserView
import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.mainFrame
import com.teamdev.jxbrowser.dsl.browser.navigation
import com.teamdev.jxbrowser.dsl.subscribe
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.navigation.event.FrameLoadFinished
import com.teamdev.jxbrowser.ui.MouseButton
import com.teamdev.jxbrowser.ui.Point
import com.teamdev.jxbrowser.ui.event.MousePressed
import com.teamdev.jxbrowser.ui.event.MouseReleased
import java.util.concurrent.CountDownLatch

/**
 * This example demonstrates how to programmatically dispatch the right click
 * mouse event to the currently loaded web page and get the event notification
 * on JavaScript.
 */
fun main() = singleWindowApplication(title = "Dispatch Mouse Events") {
    val engine = remember { Engine(RenderingMode.HARDWARE_ACCELERATED) }
    val browser = remember { engine.newBrowser() }

    Column {
        Button(onClick = { browser.dispatchMouseEvent() }) {
            Text("Dispatch Mouse Right Click")
        }
        BrowserView(browser)
    }

    DisposableEffect(Unit) {
        browser.loadHtmlAndWait()
        onDispose {
            engine.close()
        }
    }
}

// Dispatch mouse press and then release to simulate click.
private fun Browser.dispatchMouseEvent() {
    dispatch(
        MousePressed.newBuilder(Point.of(50, 50))
            .button(MouseButton.SECONDARY).clickCount(1)
            .build()
    )
    dispatch(
        MouseReleased.newBuilder(Point.of(50, 50))
            .button(MouseButton.SECONDARY).clickCount(1)
            .build()
    )
}

private fun Browser.loadHtmlAndWait() {
    val latch = CountDownLatch(1)
    navigation.subscribe<FrameLoadFinished> { latch.countDown() }
    mainFrame?.loadHtml(oncontextmenuCallback)
    awaitUninterruptibly(latch)
}

private val oncontextmenuCallback = """
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
