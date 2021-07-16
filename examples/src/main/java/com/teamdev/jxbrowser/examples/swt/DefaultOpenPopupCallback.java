/*
 *  Copyright 2021, TeamDev. All rights reserved.
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

package com.teamdev.jxbrowser.examples.swt;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.browser.callback.OpenPopupCallback;
import com.teamdev.jxbrowser.browser.event.BrowserClosed;
import com.teamdev.jxbrowser.browser.event.TitleChanged;
import com.teamdev.jxbrowser.browser.event.UpdateBoundsRequested;
import com.teamdev.jxbrowser.ui.Rect;
import com.teamdev.jxbrowser.view.swt.BrowserView;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

/**
 * The default {@link OpenPopupCallback} implementation for the SWT UI toolkit that creates and
 * shows a new window with the embedded popup browser.
 */
public final class DefaultOpenPopupCallback implements OpenPopupCallback {

    private static final int DEFAULT_POPUP_WIDTH = 800;
    private static final int DEFAULT_POPUP_HEIGHT = 600;

    @Override
    public Response on(Params params) {
        Browser browser = params.popupBrowser();
        try {
            Display display = Display.getDefault();
            display.asyncExec(() -> {
                Shell shell = new Shell(display);
                shell.setLayout(new FillLayout());
                BrowserView view = BrowserView.newInstance(shell, browser);
                updateBounds(shell, view, params.initialBounds());

                shell.addDisposeListener(event -> {
                    if (!browser.isClosed()) {
                        asyncExec(shell, browser::close);
                    }
                });

                browser.on(TitleChanged.class, event ->
                        asyncExec(shell, () -> shell.setText(event.title())));
                browser.on(BrowserClosed.class, event ->
                        asyncExec(shell, shell::dispose));
                browser.on(UpdateBoundsRequested.class, event ->
                        asyncExec(shell, () -> updateBounds(shell, view, event.bounds())));

                view.setVisible(true);
                shell.pack();
                shell.open();

                while (!shell.isDisposed()) {
                    if (!display.readAndDispatch()) {
                        display.sleep();
                    }
                }
            });
        } catch (SWTException ignore) {
            Response.proceed();
        }
        return Response.proceed();
    }

    private static void updateBounds(Shell shell, BrowserView view, Rect bounds) {
        shell.setLocation(bounds.x(), bounds.y());
        if (bounds.size().isEmpty()) {
            view.setSize(DEFAULT_POPUP_WIDTH, DEFAULT_POPUP_HEIGHT);
        } else {
            view.setSize(bounds.width(), bounds.height());
        }
    }

    private static void asyncExec(Widget widget, Runnable doRun) {
        widget.getDisplay().asyncExec(() -> {
            if (!widget.isDisposed()) {
                doRun.run();
            }
        });
    }
}
