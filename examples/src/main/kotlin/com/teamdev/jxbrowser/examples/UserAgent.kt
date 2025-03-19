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
import com.teamdev.jxbrowser.browser.Browser
import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.navigation
import com.teamdev.jxbrowser.dsl.engine.UserAgent
import com.teamdev.jxbrowser.engine.Engine
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.net.UserAgentBrand
import com.teamdev.jxbrowser.net.UserAgentData
import com.teamdev.jxbrowser.view.compose.BrowserView

/**
 * This example demonstrates how to configure the [Engine] with a custom user agent string
 * and override the user agent hints.
 */
fun main() {
    val engine = Engine(RenderingMode.OFF_SCREEN) {
        userAgent = UserAgent("Custom User Agent")
    }

    val browser = engine.newBrowser()
    setUserAgentHints(browser)

    singleWindowApplication(title = "Custom user agent") {
        BrowserView(browser)
        LaunchedEffect(Unit) {
            browser.navigation.loadUrl("https://www.whatismybrowser.com/detect/what-is-my-user-agent/")
        }
    }
}

private fun setUserAgentHints(browser: Browser) {
    val data = UserAgentData.newBuilder()
        .addBrand(UserAgentBrand.create("MyBrand", "1"))
        .addBrand(UserAgentBrand.create("MyBrand2", "2"))
        .addFormFactor("MyFormFactor")
        .fullVersion("1.0")
        .platform("MyOS")
        .platformVersion("1.0")
        .architecture("x86")
        .bitness("32")
        .model("MyModel")
        .mobile(true)
        .wow64(true)
        .build()
    browser.userAgentData(data)
}
