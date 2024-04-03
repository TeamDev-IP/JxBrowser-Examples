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

package com.teamdev.jxbrowser.examples.compose

import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.singleWindowApplication
import com.teamdev.jxbrowser.browser.Browser
import com.teamdev.jxbrowser.browser.callback.ShowContextMenuCallback
import com.teamdev.jxbrowser.compose.BrowserView
import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.mainFrame
import com.teamdev.jxbrowser.dsl.browser.navigation
import com.teamdev.jxbrowser.dsl.register
import com.teamdev.jxbrowser.dsl.removeCallback
import com.teamdev.jxbrowser.engine.RenderingMode

/**
 * This example demonstrates how to create a custom context menu using
 * [ShowContextMenuCallback] and Compose API.
 *
 * [Browser] invokes this callback when a user right-clicks on a web page.
 * We are going to create our own [DropdownMenu] and bind it to the browser
 * with the callback.
 */
fun main() = singleWindowApplication(title = "Custom Context Menu") {
    val engine = remember { Engine(RenderingMode.OFF_SCREEN) }
    val browser = remember { engine.newBrowser() }

    // Store dropdown menu state.
    var menuShowed by remember { mutableStateOf(false) }
    var menuLocation by remember { mutableStateOf(DpOffset.Zero) }
    val clipboard = LocalClipboardManager.current

    // Add browser view.
    BrowserView(browser)

    // Add custom context menu.
    DropdownMenu(
        expanded = menuShowed, // Controls menu's visibility.
        offset = menuLocation,
        onDismissRequest = { menuShowed = false },
    ) {
        DropdownMenuItem(onClick = {
            val selectedText = browser.mainFrame?.selectionAsText()!!
            clipboard.setText(AnnotatedString(selectedText))
            menuShowed = false
        }
        ) {
            Text("Copy")
        }
        DropdownMenuItem(onClick = {
            browser.navigation.goBack()
            menuShowed = false
        }) {
            Text("Back")
        }
        DropdownMenuItem(onClick = {
            browser.navigation.goForward()
            menuShowed = false
        }) {
            Text("Forward")
        }
        DropdownMenuItem(onClick = {
            browser.navigation.reload()
            menuShowed = false
        }) {
            Text("Reload")
        }
    }

    DisposableEffect(Unit) {

        // Register a callback, which shows the menu when a user right-clicks
        // on a web page
        browser.register(ShowContextMenuCallback { params, action ->
            menuLocation = with(params.location()) { DpOffset(x().dp, y().dp) }
            menuShowed = true
            action.close()
        })

        browser.navigation().loadUrl("teamdev.com/jxbrowser")

        onDispose {

            // Remove the callback when it is no longer needed.
            browser.removeCallback<ShowContextMenuCallback>()

            engine.close()
        }
    }
}
