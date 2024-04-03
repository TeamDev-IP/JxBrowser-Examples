/*
 *  Copyright 2023, TeamDev. All rights reserved.
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

import com.teamdev.jxbrowser.engine.Engine
import com.teamdev.jxbrowser.engine.EngineOptions
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.net.HttpHeader
import com.teamdev.jxbrowser.net.HttpStatus
import com.teamdev.jxbrowser.net.Scheme
import com.teamdev.jxbrowser.net.UrlRequestJob
import com.teamdev.jxbrowser.net.callback.InterceptUrlRequestCallback
import com.teamdev.jxbrowser.view.swing.BrowserView
import java.awt.BorderLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.WindowConstants

/**
 * This example demonstrates how to intercept all URL requests and handle a custom protocol.
 */
object CustomProtocol {
    private const val PROTOCOL = "jxb"
    private const val CONTENT_TYPE_HEADER_NAME = "Content-Type"
    private const val CONTENT_TYPE_HEADER_VALUE = "text/html"

    @JvmStatic
    fun main(args: Array<String>) {
        val engine = Engine.newInstance(
            EngineOptions.newBuilder(RenderingMode.HARDWARE_ACCELERATED)
                .addScheme(Scheme.of(PROTOCOL), InterceptCustomSchemeCallback())
                .build()
        )
        val browser = engine.newBrowser()

        SwingUtilities.invokeLater {
            val view = BrowserView.newInstance(browser)
            val frame = JFrame("Custom Protocol Handler")
            frame.addWindowListener(object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent) {
                    engine.close()
                }
            })
            frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            frame.add(view, BorderLayout.CENTER)
            frame.setSize(700, 500)
            frame.setLocationRelativeTo(null)
            frame.isVisible = true
        }

        browser.navigation().loadUrl(PROTOCOL + "://hello")
    }

    private class InterceptCustomSchemeCallback : InterceptUrlRequestCallback {
        override fun on(params: InterceptUrlRequestCallback.Params): InterceptUrlRequestCallback.Response? {
            val job = params.newUrlRequestJob(
                UrlRequestJob.Options.newBuilder(HttpStatus.OK)
                    .addHttpHeader(
                        HttpHeader.of(
                            CONTENT_TYPE_HEADER_NAME,
                            CONTENT_TYPE_HEADER_VALUE
                        )
                    )
                    .build()
            )
            job.write("<html><body><p>Hello there!</p></body></html>".toByteArray())
            job.complete()
            return InterceptUrlRequestCallback.Response.intercept(job)
        }
    }
}
