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
import com.teamdev.jxbrowser.dsl.engine.GoogleApiKey
import com.teamdev.jxbrowser.dsl.engine.GoogleClientId
import com.teamdev.jxbrowser.dsl.engine.GoogleClientSecret
import com.teamdev.jxbrowser.dsl.permissions
import com.teamdev.jxbrowser.dsl.register
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.permission.PermissionType.AUDIO_CAPTURE
import com.teamdev.jxbrowser.permission.callback.RequestPermissionCallback
import com.teamdev.jxbrowser.view.compose.BrowserView

/**
 * This example demonstrates how to enable voice recognition functionality
 * in Chromium engine.
 *
 * By default, the voice recognition functionality is disabled. To enable it,
 * you must provide your Google API keys to the Chromium engine as shown
 * in this example.
 *
 * The voice recognition functionality is provided by Google Cloud services,
 * in particular by Speech API. Follow the instruction below to enable it
 * and generate the required credentials.
 *
 * Enabling Speech API in Google Cloud:
 *
 * 1. Speech API is private. It is visible only to people subscribed
 *    to [chromium-dev](https://groups.google.com/a/chromium.org/g/chromium-dev)
 *    Google Group. Join this group. When joining, make sure `Link to my Google account profile`
 *    check is set (you can opt out of emails).
 * 2. Go to [console.cloud.google.com](https://console.cloud.google.com).
 *    Make sure you are logged in with the Google account associated with
 *    the email address that you used to subscribe to "chromium-dev" group.
 * 3. In the top bar, create a new project for your app or select an existing one.
 * 4. Go to APIs & Services > Library.
 * 5. Find and enable "Speech API" (NOT "Cloud Speech-to-Text API").
 *    If there's no such API found, return to the "Library" page and choose
 *    "private" in "Visibility" section on the left. There will be all private
 *    APIs visible to you. Appearance of private APIs after subscribing
 *    to the group may take some time (up to 15 minutes).
 *
 * Acquiring Keys:
 *
 * 1. Go to APIs & Services > Credentials.
 * 2. Configure OAuth consent screen, if you haven't done it before.
 *    Fill in the product name (anything you choose) and other details, then save.
 * 3. Click "CREATE CREDENTIALS" button, select "OAuth client ID" item
 *    in the drop-down list. In the “Application type” select the type that
 *    better describes your app and give it a name, then click "Create".
 * 4. In the pop-up window that appears, you'll see "Client ID" and
 *    "Client secret" strings. Keep them. They should be passed to JxBrowser.
 * 5. Click "CREATE CREDENTIALS" button again on the same page.
 *    Choose "API key". A pop-over should show up giving you the API key string.
 *    It should also be passed to JxBrowser.
 *
 * See also: [Chromium Docs | API Keys](https://chromium.googlesource.com/chromium/src.git/+/HEAD/docs/api_keys.md).
 */
fun main() {
    val engine = Engine(RenderingMode.OFF_SCREEN) {
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

    singleWindowApplication(title = "Voice recognition") {
        BrowserView(browser)
        LaunchedEffect(Unit) {
            // To test the voice recognition click the "Search by voice"
            // icon in the search field.
            browser.navigation.loadUrl("https://google.com")
        }
    }
}
