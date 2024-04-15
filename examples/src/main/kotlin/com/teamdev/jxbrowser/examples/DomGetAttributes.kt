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

import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.mainFrame
import com.teamdev.jxbrowser.dsl.frame.document
import com.teamdev.jxbrowser.dsl.dom.documentElement
import com.teamdev.jxbrowser.dsl.dom.findById
import com.teamdev.jxbrowser.engine.RenderingMode

/**
 * This example demonstrates how to get the list of existing attributes
 * of a specified HTML element.
 */
fun main(): Unit = Engine(RenderingMode.OFF_SCREEN).use { engine ->
    val browser = engine.newBrowser()
    val mainFrame = browser.mainFrame?.apply { loadHtml(HTML_LINK) }
    val document = mainFrame?.document?.documentElement
    val link = document?.findById("link")!!
    link.attributes().forEach(::println)
}

private val HTML_LINK = """
    <html lang="en">
    <body>
    <a href='#' id='link' title='link title'>Link</a>
    </body>
    </html>
""".trimIndent()
