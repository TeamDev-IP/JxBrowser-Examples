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
import com.teamdev.jxbrowser.dsl.network
import com.teamdev.jxbrowser.dsl.register
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.net.callback.VerifyCertificateCallback
import com.teamdev.jxbrowser.net.callback.VerifyCertificateCallback.Response.invalid
import com.teamdev.jxbrowser.net.callback.VerifyCertificateCallback.Response.valid
import com.teamdev.jxbrowser.net.tls.CertVerificationStatus
import com.teamdev.jxbrowser.view.compose.BrowserView

/**
 * This example demonstrates how to accept/reject SSL certificates using
 * a custom SSL certificate verifier.
 */
fun main() {
    val engine = Engine(RenderingMode.OFF_SCREEN)
    val browser = engine.newBrowser()

    engine.network.register(VerifyCertificateCallback { params ->
        val host = params.host().value()
        println("Verifying certificate for `$host`.")

        // Reject SSL certificate for all "google.com" hosts.
        if (host.contains("teamdev.com")) {
            invalid(CertVerificationStatus.AUTHORITY_INVALID)
        } else {
            valid()
        }
    })

    singleWindowApplication(title = "SSL certificate verifier") {
        BrowserView(browser)
        LaunchedEffect(Unit) {
            browser.navigation.loadUrl("https://html5test.teamdev.com/")
        }
    }
}
