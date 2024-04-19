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
import com.teamdev.jxbrowser.dsl.net.FormData
import com.teamdev.jxbrowser.dsl.net.TextData
import com.teamdev.jxbrowser.dsl.network
import com.teamdev.jxbrowser.dsl.register
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.navigation.LoadUrlParams
import com.teamdev.jxbrowser.net.callback.BeforeSendUploadDataCallback

/**
 * This example demonstrates how to override the POST payload before
 * it is sent to the server.
 */
fun main() = Engine(RenderingMode.OFF_SCREEN).use { engine ->

    engine.network.register(BeforeSendUploadDataCallback { params ->
        if (params.urlRequest().method() == "POST") {
            // Override the original POST data with a text data.
            val data = TextData("<text-data>")
            BeforeSendUploadDataCallback.Response.override(data)
        } else {
            BeforeSendUploadDataCallback.Response.proceed()
        }
    })

    val browser = engine.newBrowser()

    // Prepare form data, which is going to be overridden.
    val formData = FormData("key" to "value")
    val localhost = LoadUrlParams.newBuilder("http://localhost/")
        .uploadData(formData)
        .build()

    // Load URL request using POST method and send form data
    browser.navigation.loadUrlAndWait(localhost)
}
