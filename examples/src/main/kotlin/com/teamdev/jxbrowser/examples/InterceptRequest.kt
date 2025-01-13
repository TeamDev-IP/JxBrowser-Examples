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
import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.navigation
import com.teamdev.jxbrowser.dsl.net.HttpHeader
import com.teamdev.jxbrowser.dsl.net.UrlRequestJobOptions
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.net.HttpStatus
import com.teamdev.jxbrowser.net.Scheme
import com.teamdev.jxbrowser.net.callback.InterceptUrlRequestCallback
import com.teamdev.jxbrowser.net.callback.InterceptUrlRequestCallback.Params
import com.teamdev.jxbrowser.net.callback.InterceptUrlRequestCallback.Response
import com.teamdev.jxbrowser.view.compose.BrowserView

/**
 * This example demonstrates how to intercept a URL request and override
 * the response data in a separate thread.
 */
fun main() {
    // Create `Engine` with the custom HTTPS requests interceptor.
    val engine = Engine(RenderingMode.OFF_SCREEN) {
        schemes.add(Scheme.HTTPS, RespondWithSalutation())
    }

    val browser = engine.newBrowser()

    singleWindowApplication(title = "Intercept request") {
        BrowserView(browser)
        LaunchedEffect(Unit) {
            browser.navigation.loadUrl("https://html5test.teamdev.com/")
        }
    }
}

/**
 * An interceptor, which always sends "Hello there!" text in a response.
 */
private class RespondWithSalutation : InterceptUrlRequestCallback {

    override fun on(params: Params): Response {
        val headers = listOf(
            HttpHeader("Content-Type", "text/html"),
            HttpHeader("Content-Type", "charset=utf-8")
        )
        val options = UrlRequestJobOptions(HttpStatus.OK, headers)
        val job = params.newUrlRequestJob(options)

        // Override the response data in a separate thread.
        Thread {
            // Perform here complex calculations, data fetching/mapping, etc.
            job.write("<html><body><h1>Hello there!</h1></body></html>".toByteArray())
            job.complete()
        }.start()

        return Response.intercept(job)
    }
}
