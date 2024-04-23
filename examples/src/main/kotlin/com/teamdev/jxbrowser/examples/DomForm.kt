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
import com.teamdev.jxbrowser.dsl.browser.mainFrame
import com.teamdev.jxbrowser.dsl.browser.navigation
import com.teamdev.jxbrowser.dsl.dom.documentElement
import com.teamdev.jxbrowser.dsl.dom.findByName
import com.teamdev.jxbrowser.dsl.frame.document
import com.teamdev.jxbrowser.dsl.subscribe
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.navigation.Navigation
import com.teamdev.jxbrowser.navigation.event.FrameLoadFinished

/**
 * This example demonstrates how to fill HTML form fields using
 * JxBrowser DOM API.
 */
fun main() {
    val engine = Engine(RenderingMode.OFF_SCREEN)
    val browser = engine.newBrowser()

    singleWindowApplication(title = "DOM HTML form") {
        BrowserView(browser)
        LaunchedEffect(Unit) {
            browser.navigation.fillFormOnLoadFinished()
            browser.mainFrame?.loadHtml(HTML_FORM)
        }
    }
}

/**
 * Subscribes for [FrameLoadFinished] event to programmatically fill in
 * [HTML_FORM] when the page is loaded.
 */
private fun Navigation.fillFormOnLoadFinished() =
    subscribe<FrameLoadFinished> { event ->
        val element = event.frame().document?.documentElement
        element?.let {
            // TODO:2024-04-08:yevhenii.nadtochii: Have `element?.attribute["value"] = "John"`.
            //  See issue: https://github.com/TeamDev-IP/JxBrowser-Kotlin/issues/152
            it.findByName("firstName")!!.putAttribute("value", "John")
            it.findByName("lastName")!!.putAttribute("value", "Doe")
        }
    }

/**
 * A simple HTML form asking for a name.
 */
private val HTML_FORM = """
    <html lang="en">
    <body>
    <form name="myForm">
      First name: <input type="text" name="firstName"/><br/>
      Last name: <input type="text" name="lastName"/><br/>
      <input type="button" value="Save"/>
    </form>
    </body>
    </html>
""".trimIndent()
