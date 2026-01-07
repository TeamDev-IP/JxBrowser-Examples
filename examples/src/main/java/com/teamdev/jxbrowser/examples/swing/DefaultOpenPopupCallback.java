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

package com.teamdev.jxbrowser.examples.swing;

import static javax.swing.SwingUtilities.invokeLater;

import com.teamdev.jxbrowser.browser.callback.OpenPopupCallback;
import com.teamdev.jxbrowser.browser.event.BrowserClosed;
import com.teamdev.jxbrowser.browser.event.TitleChanged;
import com.teamdev.jxbrowser.browser.event.UpdateBoundsRequested;
import com.teamdev.jxbrowser.ui.Rect;
import com.teamdev.jxbrowser.ui.Size;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

/**
 * The default {@link OpenPopupCallback} implementation for the Swing UI toolkit that creates and
 * shows a new window with the embedded popup browser.
 */
public final class DefaultOpenPopupCallback implements OpenPopupCallback {

    private static final int DEFAULT_POPUP_WIDTH = 800;
    private static final int DEFAULT_POPUP_HEIGHT = 600;

    @Override
    public Response on(Params params) {
        var browser = params.popupBrowser();
        invokeLater(() -> {
            var view = BrowserView.newInstance(browser);
            JFrame frame = new JFrame();
            frame.add(view, BorderLayout.CENTER);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    invokeLater(browser::close);
                }
            });

            updateBounds(frame, params.initialBounds());

            browser.on(TitleChanged.class, event -> invokeLater(() ->
                    frame.setTitle(event.title())
            ));
            browser.on(BrowserClosed.class, event -> invokeLater(() -> {
                frame.setVisible(false);
                frame.dispose();
            }));
            browser.on(UpdateBoundsRequested.class, event -> invokeLater(() ->
                    updateBounds(frame, event.bounds())
            ));

            frame.setVisible(true);
        });
        return Response.proceed();
    }

    private static void updateBounds(JFrame frame, Rect bounds) {
        Size size = bounds.size();
        if (size.isEmpty()) {
            frame.setLocationByPlatform(true);
            frame.setSize(DEFAULT_POPUP_WIDTH, DEFAULT_POPUP_HEIGHT);
        } else {
            com.teamdev.jxbrowser.ui.Point origin = bounds.origin();
            frame.setLocation(new Point(origin.x(), origin.y()));
            Dimension dimension = new Dimension(size.width(), size.height());
            frame.getContentPane().setPreferredSize(dimension);
            frame.pack();
        }
    }
}
