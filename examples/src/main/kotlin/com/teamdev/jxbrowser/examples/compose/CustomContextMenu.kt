/*
 *  Copyright 2026, TeamDev. All rights reserved.
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

import androidx.compose.material.CursorDropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.window.singleWindowApplication
import com.teamdev.jxbrowser.browser.Browser
import com.teamdev.jxbrowser.browser.callback.ShowContextMenuCallback
import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.mainFrame
import com.teamdev.jxbrowser.dsl.browser.navigation
import com.teamdev.jxbrowser.dsl.register
import com.teamdev.jxbrowser.dsl.removeCallback
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.view.compose.BrowserView

/**
 * This example demonstrates how to create a custom context menu using
 * [ShowContextMenuCallback] and Compose API.
 *
 * [Browser] invokes this callback when a user right-clicks on a web page.
 * We are going to create our own [CursorDropdownMenu] and bind it to
 * the browser with the callback.
 */
fun main() = singleWindowApplication(title = "Custom context menu") {
    val engine = remember { Engine(RenderingMode.OFF_SCREEN) }
    val browser = remember { engine.newBrowser() }

    // Store dropdown menu state.
    var menuShowed by remember { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current

    // Add browser view.
    BrowserView(browser)

    // Add custom context menu.
    CursorDropdownMenu(
        expanded = menuShowed, // Controls menu's visibility.
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

        // Register a callback to display the menu
        // upon right-clicking on a web page.
        browser.register(ShowContextMenuCallback { _, tell ->
            menuShowed = true
            tell.close() // We don't need its actions.
        })

        browser.navigation().loadUrl("https://html5test.teamdev.com/")

        onDispose {

            // Remove the callback when it is no longer needed.
            browser.removeCallback<ShowContextMenuCallback>()

            engine.close()
        }
    }
}
