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

import androidx.compose.ui.window.singleWindowApplication
import com.teamdev.jxbrowser.browser.event.SpellCheckCompleted
import com.teamdev.jxbrowser.compose.BrowserView
import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.mainFrame
import com.teamdev.jxbrowser.dsl.subscribe
import com.teamdev.jxbrowser.engine.RenderingMode

/**
 * This example demonstrates how to get notifications when spell checking
 * has been completed on a web page.
 */
fun main() {
    val engine = Engine(RenderingMode.HARDWARE_ACCELERATED)
    val browser = engine.newBrowser()

    // TODO:2024-04-11:yevhenii.nadtochii: Called two items.
    //  Is it described somewhere why?
    browser.subscribe<SpellCheckCompleted> { event ->
        event.results().forEach {
            println("Error start index: " + it.location())
            println("Error length: " + it.length())
            println()
        }
    }

    browser.mainFrame?.loadHtml(MISSPELLED_TEXT)

    singleWindowApplication(title = "Spell Check Events") {
        BrowserView(browser)
    }
}

private val MISSPELLED_TEXT = """
    <html lang="en">
    <body>
    <textarea autofocus cols='30' rows='20'>Smple text with mitake.</textarea>
    </body>
    </html>
""".trimIndent()
