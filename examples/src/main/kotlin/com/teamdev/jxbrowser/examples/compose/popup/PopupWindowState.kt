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

package com.teamdev.jxbrowser.examples.compose.popup

import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import com.teamdev.jxbrowser.browser.Browser
import com.teamdev.jxbrowser.browser.callback.OpenPopupCallback
import com.teamdev.jxbrowser.browser.event.BrowserClosed
import com.teamdev.jxbrowser.browser.event.TitleChanged
import com.teamdev.jxbrowser.browser.event.UpdateBoundsRequested
import com.teamdev.jxbrowser.dsl.subscribe
import com.teamdev.jxbrowser.ui.Point
import com.teamdev.jxbrowser.ui.Rect
import com.teamdev.jxbrowser.ui.internal.ApproximateBounds
import com.teamdev.jxbrowser.ui.internal.ApproximateBounds.fuzzyEqual
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.awt.Dimension
import java.awt.Insets
import java.awt.Window
import javax.swing.JFrame.DISPOSE_ON_CLOSE

/**
 * State of [PopupWindow].
 *
 * @param [params] the parameters of [OpenPopupCallback].
 * @param [scope] the coroutine scope used to update Compose observables.
 * @param [onClose] the callback to be called when the pop-up is closed.
 */
class PopupWindowState(
    params: OpenPopupCallback.Params,
    scope: CoroutineScope,
    private val onClose: (PopupWindowState) -> Unit
) : AutoCloseable {

    private companion object {
        val DEFAULT_POPUP_SIZE = Dimension(800, 600)
    }

    /**
     * The pop-up browser.
     */
    val browser: Browser = params.popupBrowser()

    /**
     * A [ComposeWindow] to show the pop-up window.
     *
     * We intentionally avoid using the standard [Window] composable for
     * creating the windows because, at the moment, Compose has a couple of
     * bugs related to dynamic window resizing when using [WindowState].
     */
    val window: ComposeWindow = ComposeWindow().apply {
        defaultCloseOperation = DISPOSE_ON_CLOSE
        size = popupSize(params)
        location = java.awt.Point(0, 0)
    }

    init {
        browser.subscribe<TitleChanged> {
            scope.launch { window.title = it.title() }
        }
        browser.subscribe<UpdateBoundsRequested> {
            scope.launch { window.bounds(it.bounds()) }
        }
        browser.subscribe<BrowserClosed> {
            scope.launch {
                window.isVisible = false
                window.dispose()
            }
        }
    }

    /**
     * Adjusts the window size to account for insets.
     *
     * When a window is composed into a Compose layout, its dimensions already
     * include window insets. Therefore, to achieve the desired size for the
     * window content, we must add the values of these insets.
     *
     * Note that this method is only called when the window enters the
     * composition. At this point, the window becomes visible, and its
     * insets become available.
     */
    fun adjustWindowSize() {
        val currentSize = window.size
        val insets = window.insets
        window.size = currentSize.withInsets(insets)
    }

    /**
     * Sets the position and size for this [ComposeWindow] based on the
     * provided [bounds].
     *
     * This method updates the window bounds expecting the window content bounds
     * received from [UpdateBoundsRequested] event. If the screen position
     * of the window content hasn't changed, it doesn't update the window
     * position to avoid unintentional shifting.
     */
    private fun ComposeWindow.bounds(bounds: Rect) = with(bounds) {
        size = Dimension(width(), height()).withInsets(insets)
        if (origin().isDifferent()) {
            location = java.awt.Point(x(), y())
        }
    }

    /**
     * Checks if this [Point] is different from the current screen position of
     * the window content.
     *
     * It uses a fuzzy comparison, since there may be inaccuracies when
     * converting numbers after scaling.
     *
     * @see ApproximateBounds
     */
    private fun Point.isDifferent(): Boolean {
        val (x, y) = window.position
        return !fuzzyEqual(Point.of(x, y), this)
    }

    /**
     * Returns pop-up size if the one is specified in the given [params].
     *
     * Otherwise, returns the [DEFAULT_POPUP_SIZE].
     */
    private fun popupSize(params: OpenPopupCallback.Params): Dimension =
        with(params.initialBounds()) {
            if (size().isEmpty) {
                DEFAULT_POPUP_SIZE
            } else {
                Dimension(width(), height())
            }
        }

    override fun close() {
        onClose(this)
        browser.close()
    }
}

/**
 * Creates a new [Dimension] instance by adding the values of this
 * [Dimension] with the given [insets].
 */
private fun Dimension.withInsets(insets: Insets) = Dimension(
    width + insets.left + insets.right,
    height + insets.top + insets.bottom
)

/**
 * Returns the absolute position of this [Window].
 */
internal val Window.position: IntOffset
    get() = if (isVisible) {
        IntOffset(
            x = locationOnScreen.x + insets.left,
            y = locationOnScreen.y + insets.top
        )
    } else {
        IntOffset.Zero
    }
