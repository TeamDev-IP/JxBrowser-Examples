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
import com.teamdev.jxbrowser.compose.BrowserView
import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.navigation
import com.teamdev.jxbrowser.dsl.engine.GoogleApiKey
import com.teamdev.jxbrowser.dsl.engine.GoogleClientId
import com.teamdev.jxbrowser.dsl.engine.GoogleClientSecret
import com.teamdev.jxbrowser.dsl.permissions
import com.teamdev.jxbrowser.dsl.register
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.permission.PermissionType.AUDIO_CAPTURE
import com.teamdev.jxbrowser.permission.callback.RequestPermissionCallback

/**
 * This example demonstrates how to enable voice recognition functionality
 * in Chromium engine.
 *
 * By default, voice recognition functionality is disabled. To enable it,
 * you must provide your Google API Keys to the Chromium engine as shown
 * in this example.
 *
 * The instruction that describes how to acquire the API keys can be found
 * [here](https://chromium.googlesource.com/chromium/src.git/+/HEAD/docs/api_keys.md).
 * In particular, to make voice recognition work, Speech API should be enabled.
 */
fun main() {
    val engine = Engine(RenderingMode.HARDWARE_ACCELERATED) {
        google {
            apiKey = GoogleApiKey("your_api_key")
            defaultClientId = GoogleClientId("your_client_id")
            defaultClientSecret = GoogleClientSecret("your_client_secret")
        }
    }

    // Grant access to record audio.
    engine.permissions.register(RequestPermissionCallback { params, tell ->
        val type = params.permissionType()
        if (type == AUDIO_CAPTURE) {
            tell.grant()
        } else {
            tell.deny()
        }
    })

    val browser = engine.newBrowser()

    singleWindowApplication(title = "Voice Recognition") {
        BrowserView(browser)
        LaunchedEffect(Unit) {
            // To test the voice recognition click the "Search by voice"
            // icon in the search field.
            browser.navigation.loadUrl("https://google.com")
        }
    }
}
