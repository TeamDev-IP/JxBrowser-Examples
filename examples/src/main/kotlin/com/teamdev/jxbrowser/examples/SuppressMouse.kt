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
import com.teamdev.jxbrowser.browser.callback.input.PressMouseCallback
import com.teamdev.jxbrowser.compose.BrowserView
import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.mainFrame
import com.teamdev.jxbrowser.dsl.register
import com.teamdev.jxbrowser.engine.RenderingMode

/**
 * This example demonstrates how to suppress `MousePressed` event
 * using [PressMouseCallback].
 *
 * For suppressing other mouse events the following callbacks are used:
 *
 * - `EnterMouseCallback`
 * - `ExitMouseCallback`
 * - `MoveMouseCallback`
 * - `MoveMouseWheelCallback`
 * - `ReleaseMouseCallback`
 */
fun main() {
    val engine = Engine(RenderingMode.HARDWARE_ACCELERATED)
    val browser = engine.newBrowser()

    browser.register(PressMouseCallback { params ->
        if (params.event().keyModifiers().isShiftDown) {
            PressMouseCallback.Response.proceed()
        } else {
            PressMouseCallback.Response.suppress()
        }
    })

    singleWindowApplication(title = "Suppress the Mouse Pressed event") {
        BrowserView(browser)
        LaunchedEffect(Unit) {
            browser.mainFrame?.loadHtml(HTML_BUTTON)
        }
    }
}

private val HTML_BUTTON = """
    <button onclick="clicked()">click holding shift</button>
    <script>
      function clicked() {
          alert('clicked');
      }
    </script>
""".trimIndent()
