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
import com.teamdev.jxbrowser.dsl.browser.navigation
import com.teamdev.jxbrowser.dsl.network
import com.teamdev.jxbrowser.dsl.register
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.net.callback.CanGetCookiesCallback
import com.teamdev.jxbrowser.net.callback.CanSetCookieCallback

/**
 * This example demonstrates how to suppress/filter all the incoming
 * and outgoing cookies.
 */
fun main() {
    val engine = Engine(RenderingMode.OFF_SCREEN)

    // Suppress/filter all the incoming and outgoing cookies.
    engine.network.apply {
        register(CanSetCookieCallback { params ->
            println("Disallow accepting cookies for: " + params.url())
            CanSetCookieCallback.Response.cannot()
        })
        register(CanGetCookiesCallback { params ->
            println("Disallow sending cookies for: " + params.url())
            CanGetCookiesCallback.Response.cannot()
        })
    }

    val browser = engine.newBrowser()
    browser.navigation.loadUrlAndWait("https://google.com")

    // The suppressed cookies have been printed to the console.
    engine.close()
}
