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
import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.navigation
import com.teamdev.jxbrowser.dsl.network
import com.teamdev.jxbrowser.dsl.register
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.net.ResourceType
import com.teamdev.jxbrowser.net.callback.BeforeUrlRequestCallback
import com.teamdev.jxbrowser.view.compose.BrowserView

/**
 * This example demonstrates how to handle all resources such as HTML, PNG,
 * JavaScript, CSS files, and decide whether web browser engine should load
 * them from a web server.
 *
 * In this snippet we cancel loading of all images.
 */
fun main() {
    val engine = Engine(RenderingMode.OFF_SCREEN)

    engine.network.register(BeforeUrlRequestCallback { params ->
        if (params.urlRequest().resourceType() == ResourceType.IMAGE) {
            BeforeUrlRequestCallback.Response.cancel()
        } else {
            BeforeUrlRequestCallback.Response.proceed()
        }
    })

    val browser = engine.newBrowser()

    singleWindowApplication(title = "Filter images") {
        BrowserView(browser)
        LaunchedEffect(Unit) {
            browser.navigation.loadUrl("https://www.google.com")
        }
    }
}
