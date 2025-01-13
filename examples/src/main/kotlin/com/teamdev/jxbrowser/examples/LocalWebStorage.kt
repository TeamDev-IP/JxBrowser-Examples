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

import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.mainFrame
import com.teamdev.jxbrowser.dsl.browser.navigation
import com.teamdev.jxbrowser.dsl.frame.localStorage
import com.teamdev.jxbrowser.dsl.frame.set
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.frame.Frame

/**
 * This example demonstrates how to access the local storage and perform
 * operations with it.
 *
 * Accessing the session storage is performed by calling [Frame.sessionStorage]
 * instead of [Frame.localStorage].
 */
fun main() = Engine(RenderingMode.OFF_SCREEN).use { engine ->
    val browser = engine.newBrowser()
    browser.navigation.loadUrlAndWait("https://html5test.teamdev.com/")

    val frame = browser.mainFrame!!
    frame.localStorage[KEY] = "Tom"

    val value = frame.executeJavaScript<String>("window.localStorage.getItem(\"$KEY\")")
    println("$value")
}

private const val KEY = "Name"
