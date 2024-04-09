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
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.window.singleWindowApplication
import com.teamdev.jxbrowser.browser.Browser
import com.teamdev.jxbrowser.compose.BrowserView
import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.navigation
import com.teamdev.jxbrowser.dsl.browser.textFinder
import com.teamdev.jxbrowser.engine.RenderingMode

/**
 * This example demonstrates how to find text on the loaded web page.
 */
fun main() = singleWindowApplication(title = "Find Text") {

    // TODO:2024-04-05:yevhenii.nadtochii: Fails in HARDWARE_ACCELERATED mode.
    // See issue: https://github.com/TeamDev-IP/JxBrowser-Kotlin/issues/150
    val engine = remember { Engine(RenderingMode.HARDWARE_ACCELERATED) }
    val browser = remember { engine.newBrowser() }
    var searchedText by remember { mutableStateOf("") }

    Column {
        TextField(value = searchedText, onValueChange = {
            searchedText = it
            browser.printNumberOfMatches(it)
        })
        BrowserView(browser)
    }

    LaunchedEffect(Unit) {
        browser.navigation.loadUrl("https://www.google.com")
    }
}

private fun Browser.printNumberOfMatches(searchedText: String) =
    if (searchedText.isNotEmpty()) {
        textFinder.find(searchedText) {
            println("Matches found: ${it.numberOfMatches()}.")
        }
    } else {
        textFinder.stopFindingAndClearSelection()
    }