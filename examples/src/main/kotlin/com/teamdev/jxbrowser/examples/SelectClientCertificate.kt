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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.singleWindowApplication
import com.teamdev.jxbrowser.browser.callback.SelectClientCertificateCallback
import com.teamdev.jxbrowser.compose.BrowserView
import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.navigation
import com.teamdev.jxbrowser.dsl.register
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.net.tls.ClientCertificate
import com.teamdev.jxbrowser.net.tls.SslPrivateKey
import java.security.cert.X509Certificate
import kotlin.jvm.optionals.getOrNull

/**
 * This example demonstrates how to display "Select Client SSL Certificate"
 * dialog where you must select a required SSL certificate to continue loading
 * web page.
 *
 * **Important:** before you run this example, please follow the instruction
 * at [badssl.com](https://badssl.com/download/) and install the required
 * custom SSL certificate.
 */
fun main() = singleWindowApplication(title = "Select client SSL certificate") {
    val engine = remember { Engine(RenderingMode.OFF_SCREEN) }
    val browser = remember { engine.newBrowser() }

    var x509Certificates by remember { mutableStateOf(emptyList<X509Certificate>()) }
    var onCertificateSelected by remember { mutableStateOf<(X509Certificate) -> Unit>({ }) }

    BrowserView(browser)

    if (x509Certificates.isNotEmpty()) {
        SelectionDialog(
            certificates = x509Certificates,
            onSelected = onCertificateSelected
        )
    }

    LaunchedEffect(Unit) {
        browser.register(SelectClientCertificateCallback { params, tell ->
            val certificates = params.certificates()
            if (certificates.isEmpty()) {
                tell.cancel()
            }

            x509Certificates = certificates
                .map { it.toX509Certificate() }
                .mapNotNull { it.getOrNull() }

            onCertificateSelected = { selectedCertificate ->

                x509Certificates = emptyList()

                // TODO:2024-04-10:yevhenii.nadtochii: Not working.
                //  See issue: https://github.com/TeamDev-IP/JxBrowser-Docs/issues/932
                val privateKey = SslPrivateKey.of(selectedCertificate.encoded)
                val clientCertificate = ClientCertificate.of(selectedCertificate, privateKey)

                // Pass the selected certificate to the engine.
                tell.select(clientCertificate)
            }
        })

        browser.navigation.loadUrl("https://client.badssl.com/")
    }
}

@Composable
private fun SelectionDialog(
    certificates: List<X509Certificate>,
    onSelected: (X509Certificate) -> Unit,
) = Dialog(
    onDismissRequest = { /* Selection is mandatory. */ },
    properties = DialogProperties(usePlatformDefaultWidth = false)
) {
    Card(
        modifier = Modifier.size(600.dp, 300.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Select a certificate: ")
            SelectionList(certificates, onSelected)
        }
    }
}

@Composable
private fun SelectionList(
    certificates: List<X509Certificate>,
    onSelected: (X509Certificate) -> Unit
) {
    var selected by remember { mutableStateOf<X509Certificate?>(null) }
    Column {
        certificates.forEach { certificate ->
            Text(
                text = certificate.subjectX500Principal.name,
                modifier = Modifier
                    .selectable(
                        selected = certificate == selected,
                        onClick = {
                            selected = certificate
                            onSelected(certificate)
                        }
                    )
                    .background(
                        if (selected == certificate) Color.Gray
                        else Color.Transparent
                    )
                    .padding(8.dp)
            )
        }
    }
}
