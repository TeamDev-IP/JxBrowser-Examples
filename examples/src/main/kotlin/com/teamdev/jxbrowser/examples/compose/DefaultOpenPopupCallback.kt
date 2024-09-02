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

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.window.singleWindowApplication
import com.teamdev.jxbrowser.browser.callback.CreatePopupCallback
import com.teamdev.jxbrowser.browser.callback.OpenPopupCallback

import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.register
import com.teamdev.jxbrowser.dsl.removeCallback
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.view.compose.BrowserView
import com.teamdev.jxbrowser.view.compose.popup.PopupWindow
import com.teamdev.jxbrowser.view.compose.popup.PopupWindowState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * This example demonstrates the default [OpenPopupCallback] implementation
 * for Compose UI toolkit.
 *
 * It creates and shows a new window with the embedded pop-up browser
 * each time [OpenPopupCallback] is invoked.
 */
fun main() = singleWindowApplication(title = "Default `OpenPopupCallback`") {
    val engine = remember { Engine(RenderingMode.OFF_SCREEN) }
    val browser = remember { engine.newBrowser() }

    // Store pop-up windows in Compose observable list.
    val popups = remember { mutableStateListOf<PopupWindowState>() }

    // Remember the coroutine scope to update the pop-up window after creation.
    // For example, to pick up page's title then it completes loading.
    val scope = rememberCoroutineScope()

    // Add browser view.
    BrowserView(browser)

    // Add pop-up windows, if any.
    for (popup in popups) {
        // We associate each `PopupWindow` with a unique key. This helps Compose
        // efficiently manage the lifecycle of pop-up components when the list
        // of pop-ups changes.
        key(popup) {
            PopupWindow(popup)
        }
    }

    DisposableEffect(Unit) {

        // This callback decides whether a pop-up should be created.
        // `OpenPopupCallback` callback will NOT be called if this one returns
        // `suppress` instead of `create`.
        browser.register(CreatePopupCallback {
            CreatePopupCallback.Response.create()
        })

        // `OpenPopupCallback` is responsible for the pop-up window creation.
        browser.register(OpenPopupCallback { params ->
            scope.launch {
                popups.addNewPopup(params, scope) // Adds a new pop-up to the list.
            }
            OpenPopupCallback.Response.proceed()
        })

        browser.navigation().loadUrl("teamdev.com/jxbrowser")

        onDispose {

            // Remove the callbacks when they are no longer needed.
            browser.removeCallback<CreatePopupCallback>()
            browser.removeCallback<OpenPopupCallback>()

            engine.close()
        }
    }
}

/**
 * Creates a new instance of [PopupWindowState] and adds it to
 * this [SnapshotStateList].
 *
 * The created pop-up will be removed from the list automatically
 * when it is closed via [PopupWindowState.onClose] callback.
 */
fun SnapshotStateList<PopupWindowState>.addNewPopup(
    params: OpenPopupCallback.Params,
    scope: CoroutineScope,
) = add(
    PopupWindowState(
        browser = params.popupBrowser(),
        bounds = params.initialBounds(),
        scope = scope,
        onClose = { remove(it) }
    )
)
