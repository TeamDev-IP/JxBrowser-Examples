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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.singleWindowApplication
import com.teamdev.jxbrowser.browser.callback.SelectClientCertificateCallback
import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.navigation
import com.teamdev.jxbrowser.dsl.register
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.view.compose.BrowserView
import java.security.cert.X509Certificate
import kotlin.jvm.optionals.getOrNull

/**
 * This example demonstrates how to display "Select Client SSL Certificate"
 * dialog where an end-user must select an SSL certificate to continue loading
 * the web page.
 *
 * **Important:** before you run this example, please follow the instruction
 * at [badssl.com](https://badssl.com/download/) and install the required
 * custom SSL certificate.
 */
fun main() = singleWindowApplication(title = "Select client SSL certificate") {
    val engine = remember { Engine(RenderingMode.OFF_SCREEN) }
    val browser = remember { engine.newBrowser() }

    var certificates by remember { mutableStateOf(emptyList<X509Certificate>()) }
    var onSelected by remember { mutableStateOf<(Int) -> Unit>({ }) }

    BrowserView(browser)

    if (certificates.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { /* Selection is mandatory. */ },
            modifier = Modifier.size(800.dp, 300.dp),
            text = { Text("Select a certificate: ") },
            buttons = { SelectionList(certificates, onSelected) }
        )
    }

    LaunchedEffect(Unit) {
        browser.register(SelectClientCertificateCallback { params, tell ->
            certificates = params.certificates()
                .mapNotNull { it.toX509Certificate().getOrNull() }

            if (certificates.isEmpty()) {
                tell.cancel()
            }

            onSelected = { index ->
                certificates = emptyList()
                tell.select(index)
            }
        })

        browser.navigation.loadUrl("https://client.badssl.com/")
    }
}

@Composable
private fun SelectionList(
    certificates: List<X509Certificate>,
    onSelected: (Int) -> Unit
) {
    var selected by remember { mutableStateOf(-1) }
    Column {
        certificates.forEachIndexed { index, certificate ->
            Text(
                text = certificate.subjectX500Principal.name,
                modifier = Modifier
                    .selectable(
                        selected = index == selected,
                        onClick = {
                            selected = index
                            onSelected(index)
                        }
                    )
                    .background(
                        if (selected == index) Color.Gray
                        else Color.Transparent
                    )
                    .padding(8.dp)
            )
        }
    }
}
