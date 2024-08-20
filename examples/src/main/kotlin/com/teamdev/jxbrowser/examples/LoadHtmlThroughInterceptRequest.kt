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
import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.navigation
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.net.HttpStatus
import com.teamdev.jxbrowser.net.Scheme
import com.teamdev.jxbrowser.net.callback.InterceptUrlRequestCallback
import com.teamdev.jxbrowser.net.callback.InterceptUrlRequestCallback.Response.intercept
import com.teamdev.jxbrowser.net.callback.InterceptUrlRequestCallback.Response.proceed
import com.teamdev.jxbrowser.net.internal.rpc.httpHeader
import com.teamdev.jxbrowser.net.internal.rpc.netString
import com.teamdev.jxbrowser.net.internal.rpc.urlRequestJobOptions
import com.teamdev.jxbrowser.view.compose.BrowserView

/**
 * This example demonstrates how to load HTML by intercepting URL requests.
 */
fun main() {
    // Create a custom URL interceptor.
    val loadHTML = InterceptUrlRequestCallback { params ->
        if (!params.urlRequest().url().endsWith("?load-html")) {
            proceed()
        }
        val bytes = "<html><body>Hello!</body></html>".toByteArray()
        val options = urlRequestJobOptions {
            httpStatus = HttpStatus.OK.value()
            httpHeader.add(
                httpHeader {
                    name = "content-type"
                    value = netString {
                        utf8String = "text/html"
                    }
                }
            )
        }
        val job = params.newUrlRequestJob(options).apply {
            write(bytes)
            complete()
        }
        intercept(job)
    }

    // Create `Engine` with the custom HTTP requests interceptor.
    val engine = Engine(RenderingMode.OFF_SCREEN) {
        schemes.add(Scheme.HTTP, loadHTML)
    }

    val browser = engine.newBrowser()
    browser.navigation.loadUrl("http://localhost?load-html")

    singleWindowApplication(title = "Load HTML through interception") {
        BrowserView(browser)
    }
}
