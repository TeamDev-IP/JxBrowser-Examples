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

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
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
import com.teamdev.jxbrowser.net.callback.InterceptUrlRequestCallback.Params
import com.teamdev.jxbrowser.net.callback.InterceptUrlRequestCallback.Response

/**
 * URL protocol for custom handling.
 */
private val PROTOCOL = Scheme.of("jxb")

/**
 * This example demonstrates how to intercept all URL requests and handle
 * a custom protocol.
 */
fun main() = singleWindowApplication(title = "Custom Protocol Handler") {
    val engine = remember { createEngine() }
    val browser = remember { engine.newBrowser() }
    BrowserView(browser)
    DisposableEffect(Unit) {
        browser.navigation.loadUrl("${PROTOCOL.name()}://hello")
        onDispose {
            engine.close()
        }
    }
}

/**
 * Creates a new [Engine] with the custom requests interceptor for
 * the given [PROTOCOL].
 */
private fun createEngine() = Engine(RenderingMode.HARDWARE_ACCELERATED) {
    options {
        schemes {
            add(PROTOCOL, RespondWithGreetings())
        }
    }
}

/**
 * An interceptor, which always sends "Hello there!" text in a response.
 */
private class RespondWithGreetings : InterceptUrlRequestCallback {

    override fun on(params: Params): Response {
        val options = UrlRequestJob.Options.newBuilder(HttpStatus.OK)
            .addHttpHeader(HttpHeader.of("Content-Type", "text/html"))
            .build()
        val job = params.newUrlRequestJob(options).apply {
            write("<html><body><p>Hello there!</p></body></html>".toByteArray())
            complete()
        }
        return Response.intercept(job)
    }
}
