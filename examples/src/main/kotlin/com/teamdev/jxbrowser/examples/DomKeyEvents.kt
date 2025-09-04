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

package com.teamdev.jxbrowser.examples

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.singleWindowApplication
import com.google.common.util.concurrent.Uninterruptibles.awaitUninterruptibly
import com.teamdev.jxbrowser.browser.Browser
import com.teamdev.jxbrowser.dom.Element
import com.teamdev.jxbrowser.dom.event.Event
import com.teamdev.jxbrowser.dom.event.EventType
import com.teamdev.jxbrowser.dom.event.KeyEvent
import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.mainFrame
import com.teamdev.jxbrowser.dsl.browser.navigation
import com.teamdev.jxbrowser.dsl.dom.findById
import com.teamdev.jxbrowser.dsl.frame.document
import com.teamdev.jxbrowser.dsl.subscribe
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.navigation.event.FrameLoadFinished
import com.teamdev.jxbrowser.view.compose.BrowserView
import java.util.concurrent.CountDownLatch

/**
 * This examples demonstrates how to capture keyboard events from the DOM nodes.
 */
fun main() {
    val engine = Engine(RenderingMode.OFF_SCREEN)
    val browser = engine.newBrowser()

    singleWindowApplication(title = "DOM keyboard event listener") {
        BrowserView(browser)
        LaunchedEffect(Unit) {
            browser.loadHtmlAndWait(HTML_FIELD)
            browser.findField(FIELD_ID)?.subscribeForKeyboardEvents()
        }
    }
}

private fun Browser.loadHtmlAndWait(html: String) {
    val latch = CountDownLatch(1)
    navigation.subscribe<FrameLoadFinished> { latch.countDown() }
    navigation.loadHtml(html)
    awaitUninterruptibly(latch)
}

private fun Browser.findField(id: String): Element? =
    mainFrame?.document?.findById(id)

private fun Element.subscribeForKeyboardEvents() {
    val useCapture = false
    addEventListener(EventType.KEY_DOWN, ::printEventDetails, useCapture)
    addEventListener(EventType.KEY_PRESS, ::printEventDetails, useCapture)
    addEventListener(EventType.KEY_UP, ::printEventDetails, useCapture)
}

private fun printEventDetails(event: Event) {
    val keyEvent = event as KeyEvent
    val message = KEY_EVENT_INFO
        .format(
            keyEvent.type().value(),
            keyEvent.character(),
            keyEvent.domKeyCode()
        )
    println(message)
    println()
}

private const val FIELD_ID = "my-field"
private const val HTML_FIELD = "<input type='text' id='$FIELD_ID' />"
private val KEY_EVENT_INFO = """
    Event type: `%s`. 
    Typed character (if applicable): `%s`. 
    Key: `%s`.
""".trimIndent()
