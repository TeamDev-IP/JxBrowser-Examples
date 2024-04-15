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
import com.teamdev.jxbrowser.compose.BrowserView
import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.navigation
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.net.HttpHeader
import com.teamdev.jxbrowser.net.HttpStatus
import com.teamdev.jxbrowser.net.Scheme
import com.teamdev.jxbrowser.net.UrlRequestJob
import com.teamdev.jxbrowser.net.callback.InterceptUrlRequestCallback
import com.teamdev.jxbrowser.net.callback.InterceptUrlRequestCallback.*

/**
 * This example demonstrates how to intercept a URL request and override
 * the response data in a separate thread.
 */
fun main() {
    // Create `Engine` with the custom HTTPS requests interceptor.
    val engine = Engine(RenderingMode.HARDWARE_ACCELERATED) {
        schemes.add(Scheme.HTTPS, RespondWithSalutation())
    }

    val browser = engine.newBrowser()
    browser.navigation.loadUrl("https://www.google.com")

    singleWindowApplication(title = "Intercept Request") {
        BrowserView(browser)
    }
}

/**
 * An interceptor, which always sends "Hello there!" text in a response.
 */
private class RespondWithSalutation : InterceptUrlRequestCallback {

    private companion object {
        private const val HTML_GREETING =
            "<html><body><h1>Hello there!</h1></body></html>"
    }

    override fun on(params: Params): Response {
        val options = UrlRequestJob.Options.newBuilder(HttpStatus.OK)
            .addHttpHeader(HttpHeader.of("Content-Type", "text/html"))
            .addHttpHeader(HttpHeader.of("Content-Type", "charset=utf-8"))
            .build()
        val job = params.newUrlRequestJob(options)

        // Perform complex calculations and override the response data
        // in a separate thread.
        Thread {
            job.write(HTML_GREETING.toByteArray())
            job.complete()
        }.start()

        return Response.intercept(job)
    }
}
