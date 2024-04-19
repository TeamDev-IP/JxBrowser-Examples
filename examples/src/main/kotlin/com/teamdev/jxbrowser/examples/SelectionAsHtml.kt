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

import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.singleWindowApplication
import com.teamdev.jxbrowser.compose.BrowserView
import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.mainFrame
import com.teamdev.jxbrowser.dsl.browser.navigation
import com.teamdev.jxbrowser.engine.RenderingMode

/**
 * This example demonstrates how to obtain the HTML content from the selected
 * part of the loaded web page.
 */
fun main() {
    val engine = Engine(RenderingMode.OFF_SCREEN)
    val browser = engine.newBrowser()
    val frame = browser.mainFrame!!

    singleWindowApplication(title = "Get selected HTML") {
        var selectedHtml by remember { mutableStateOf("") }

        Column {
            Button(onClick = { selectedHtml = frame.selectionAsHtml() }) {
                Text("Get Selected HTML")
            }
            BrowserView(browser)
        }

        if (selectedHtml.isNotEmpty()) {
            InfoDialog(
                text = selectedHtml,
                onDismiss = { selectedHtml = "" }
            )
        }

        LaunchedEffect(Unit) {
            browser.navigation.loadUrl("https://www.teamdev.com/jxbrowser")
        }
    }
}

@Composable
private fun InfoDialog(text: String, onDismiss: () -> Unit) =
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        },
        text = { Text(text) },
    )
