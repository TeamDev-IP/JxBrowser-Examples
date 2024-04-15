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
import com.teamdev.jxbrowser.net.UrlRequest
import com.teamdev.jxbrowser.net.UrlRequestJob
import com.teamdev.jxbrowser.net.callback.InterceptUrlRequestCallback
import com.teamdev.jxbrowser.net.callback.InterceptUrlRequestCallback.*
import com.teamdev.jxbrowser.net.callback.InterceptUrlRequestCallback.Response.intercept
import com.teamdev.jxbrowser.net.callback.InterceptUrlRequestCallback.Response.proceed
import java.net.URL
import java.net.URLConnection

/**
 * This example demonstrates how to register a JAR protocol handler
 * to load files located inside a JAR archive.
 */
fun main() {
    // Create an engine with the custom JAR requests interceptor.
    val engine = Engine(RenderingMode.HARDWARE_ACCELERATED) {
        schemes.add(Scheme.JAR, InterceptJarRequestCallback())
    }

    val browser = engine.newBrowser()

    // Load `index.html` file located inside `tiny-website.jar` added
    // to this Java app runtime classpath.
    val website = object {}.javaClass.getResource("/resources/index.html")!!
    browser.navigation.loadUrl("$website")

    singleWindowApplication(title = "JAR Protocol Handler") {
        BrowserView(browser)
    }
}

/**
 * Overrides response data for requests that ask for a file from Jar archives.
 *
 * Such interceptor is needed when Chromium requests files from Jar archives.
 * Such requests take place when an initially loaded page itself is located
 * in the application resources (and thus, in a Jar archive).
 *
 * This page may initiate loading of other resources: style sheets, images,
 * scripts, etc. And they all are located in a Jar just like `index.html`.
 * Their paths are usually relative. Chromium can't load them on its own.
 *
 * This class intercepts such requests, loads the files on its own
 * and overrides response data.
 */
private class InterceptJarRequestCallback : InterceptUrlRequestCallback {

    override fun on(params: Params): Response {

        // Check if the requested URL points to a file inside a Jar archive.
        val request = params.urlRequest()
        if (!request.url().startsWith("jar:")) {
            // Do nothing if not.
            return proceed()
        }

        // Read the requested file from the resources.
        val javaUrl = request.toJavaUrl()
        val entry = JarEntry(javaUrl)

        // Override response data.
        val job = overrideResponse(entry, params)
        return intercept(job)
    }

    private fun overrideResponse(
        entry: JarEntry,
        params: Params
    ): UrlRequestJob {
        val contentType = HttpHeader.of("Content-Type", entry.mimeType)
        val options = UrlRequestJob.Options.newBuilder(HttpStatus.OK)
            .addHttpHeader(contentType)
            .build()
        val job = params.newUrlRequestJob(options).apply {
            write(entry.data)
            complete()
        }
        return job
    }
}

/**
 * Converts this [UrlRequest] from Chromium notation
 * to [Java's one][java.net.JarURLConnection].
 *
 * In particular, `jar://file/path` is converted to `jar:file:/path`.
 */
private fun UrlRequest.toJavaUrl(): URL {
    val javaSpec = url().replace("jar://file", "jar:file:")
    return URL(javaSpec)
}

/**
 * Represents a file inside a Jar archive.
 */
private class JarEntry(url: URL) {
    val mimeType = URLConnection.guessContentTypeFromName(url.file) ?: ""
    val data by lazy { url.readBytes() }
}
