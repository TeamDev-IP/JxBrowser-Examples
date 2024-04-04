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
import com.teamdev.jxbrowser.ui.KeyCode
import com.teamdev.jxbrowser.ui.event.KeyPressed
import com.teamdev.jxbrowser.ui.event.KeyReleased
import com.teamdev.jxbrowser.ui.event.KeyTyped
import java.util.concurrent.CountDownLatch

/**
 * This example demonstrates how to dispatch the `KeyEvent` to the currently
 * focused element on the loaded web page.
 */
fun main() = singleWindowApplication(title = "Dispatch Key Event") {
    val engine = remember { Engine(RenderingMode.OFF_SCREEN) }
    val browser = remember { engine.newBrowser() }
    BrowserView(browser)
    DisposableEffect(Unit) {
        with(browser) {
            loadHtmlAndWait("<input id=\"input\" autofocus>")
            dispatchKeyEvent('h')
            dispatchKeyEvent('i')
        }
        onDispose {
            engine.close()
        }
    }
}

private fun Browser.loadHtmlAndWait(html: String) {
    val latch = CountDownLatch(1)
    mainFrame?.loadHtml(html)
    navigation.subscribe<FrameLoadFinished> { latch.countDown() }
    awaitUninterruptibly(latch)
}

private fun Browser.dispatchKeyEvent(character: Char) {
    val keyCode = KEY_CODES[character]!!
    dispatch(
        KeyPressed.newBuilder(keyCode)
            .keyChar(character)
            .build()
    )
    dispatch(
        KeyTyped.newBuilder(keyCode)
            .keyChar(character)
            .build()
    )
    dispatch(
        KeyReleased.newBuilder(keyCode)
            .build()
    )
}

private val KEY_CODES = mapOf(
    'h' to KeyCode.KEY_CODE_H,
    'i' to KeyCode.KEY_CODE_I
)
