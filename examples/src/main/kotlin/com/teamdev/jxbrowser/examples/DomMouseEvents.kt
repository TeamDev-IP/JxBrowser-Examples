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
import com.google.common.util.concurrent.Uninterruptibles.awaitUninterruptibly
import com.teamdev.jxbrowser.browser.Browser
import com.teamdev.jxbrowser.compose.BrowserView
import com.teamdev.jxbrowser.dom.Element
import com.teamdev.jxbrowser.dom.event.Event
import com.teamdev.jxbrowser.dom.event.EventType
import com.teamdev.jxbrowser.dom.event.MouseEvent
import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.mainFrame
import com.teamdev.jxbrowser.dsl.browser.navigation
import com.teamdev.jxbrowser.dsl.dom.findById
import com.teamdev.jxbrowser.dsl.frame.document
import com.teamdev.jxbrowser.dsl.subscribe
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.navigation.event.FrameLoadFinished
import java.util.concurrent.CountDownLatch

/**
 * This examples demonstrates how to capture mouse events from the DOM nodes.
 */
fun main() {
    val engine = Engine(RenderingMode.HARDWARE_ACCELERATED)
    val browser = engine.newBrowser()
    singleWindowApplication(title = "DOM mouse event listener") {
        BrowserView(browser)
        LaunchedEffect(Unit) {
            browser.loadHtmlAndWait(HTML_BUTTON)
            browser.findButton(BUTTON_ID)?.subscribeToMouseEvents()
        }
    }
}

private fun Browser.loadHtmlAndWait(html: String) {
    val latch = CountDownLatch(1)
    navigation.subscribe<FrameLoadFinished> { latch.countDown() }
    mainFrame?.loadHtml(html)
    awaitUninterruptibly(latch)
}

private fun Browser.findButton(id: String): Element? =
    mainFrame?.document?.findById(id)

private fun Element.subscribeToMouseEvents() {
    val useCapture = false
    addEventListener(EventType.MOUSE_DOWN, ::printEventDetails, useCapture)
    addEventListener(EventType.MOUSE_UP, ::printEventDetails, useCapture)
    addEventListener(EventType.MOUSE_OVER, ::printEventDetails, useCapture)
}

private fun printEventDetails(event: Event) {
    val mouseEvent = event as MouseEvent
    val location = mouseEvent.pageLocation()
    val message = MOUSE_EVENT_INFO.format(
        mouseEvent.type().value(),
        mouseEvent.button(),
        location.x(),
        location.y()
    )
    println(message)
    println()
}

private const val BUTTON_ID = "button"
private const val HTML_BUTTON = "<button id='$BUTTON_ID'>Click me</button>"
private val MOUSE_EVENT_INFO = """
    Event type: `%s`. 
    Button: `%s`. 
    Page location: `(%d, %d)`.
""".trimIndent()
