/*
 *  Copyright 2025, TeamDev. All rights reserved.
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

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;
import static javax.swing.SwingUtilities.invokeLater;

import com.teamdev.jxbrowser.browser.callback.ShowContextMenuCallback;
import com.teamdev.jxbrowser.browser.internal.BrowserImpl;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.frame.Frame;
import com.teamdev.jxbrowser.internal.rpc.stream.Interceptor.Action;
import com.teamdev.jxbrowser.ui.event.internal.rpc.MoveMouseWheel;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.WindowConstants;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * This example demonstrates how to build a custom context menu using Swing API and show it when
 * user right-click on the web page.
 */
public final class CustomContextMenu {

    public static void main(String[] args) {
        var engine = Engine.newInstance(HARDWARE_ACCELERATED);
        var browser = engine.newBrowser();

        invokeLater(() -> {
            var view = BrowserView.newInstance(browser);
            browser.set(ShowContextMenuCallback.class, new ShowCustomContextMenu(view));

            var frame = new JFrame("JxBrowser: Swing Context Menu");
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    engine.close();
                }
            });
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(view, BorderLayout.CENTER);
            frame.setSize(900, 700);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        browser.navigation().loadUrl("https://html5test.teamdev.com/");
    }

    private static class ShowCustomContextMenu implements ShowContextMenuCallback {

        private final BrowserView parent;

        ShowCustomContextMenu(BrowserView parent) {
            this.parent = parent;
        }

        @Override
        public void on(Params params, Action action) {
            new SwingContextMenu(parent, params, action) {
                @Override
                protected void initialize(JPopupMenu contextMenu) {
                    var browser = parent.getBrowser();

                    var goBackMenuItem = new JMenuItem("Back");
                    goBackMenuItem.addActionListener(
                            e -> browser.navigation().goBack());

                    var goForwardMenuItem = new JMenuItem("Forward");
                    goForwardMenuItem.addActionListener(
                            e -> browser.navigation().goForward());

                    var reloadMenuItem = new JMenuItem("Reload");
                    reloadMenuItem.addActionListener(
                            e -> browser.navigation().reload());

                    var printMenuItem = new JMenuItem("Print...");
                    printMenuItem.addActionListener(
                            e -> browser.mainFrame().ifPresent(Frame::print));

                    contextMenu.add(goBackMenuItem);
                    contextMenu.add(goForwardMenuItem);
                    contextMenu.add(reloadMenuItem);
                    contextMenu.addSeparator();
                    contextMenu.add(printMenuItem);
                }
            }.show();
        }
    }

    private static abstract class SwingContextMenu {

        private final BrowserView parent;
        private final BrowserImpl browser;
        protected final ShowContextMenuCallback.Params params;
        protected final ShowContextMenuCallback.Action callback;

        SwingContextMenu(BrowserView parent, ShowContextMenuCallback.Params params,
                ShowContextMenuCallback.Action callback) {
            this.parent = parent;
            this.browser = (BrowserImpl) parent.getBrowser();
            this.params = params;
            this.callback = callback;
        }

        protected abstract void initialize(JPopupMenu contextMenu);

        public final void show() {
            var popupMenu = new JPopupMenu();
            popupMenu.setLightWeightPopupEnabled(false);
            popupMenu.addPopupMenuListener(
                    new SwingContextMenu.PopupMenuListenerImpl());
            initialize(popupMenu);
            registerMouseCallbackInterceptors(popupMenu);
            var location = params.location();
            int x = location.x();
            int y = location.y();
            invokeLater(() -> popupMenu.show(parent, x, y));
        }

        private void notifyContextMenuClosed() {
            unregisterMouseCallbackInterceptors();
            if (!callback.isClosed()) {
                callback.close();
            }

        }

        private void registerMouseCallbackInterceptors(JPopupMenu popupMenu) {
            if (isHardwareAccelerated()) {
                browser.setCallbackInterceptor(MoveMouseWheel.Request.class, (params) -> {
                    hide(popupMenu);
                    return Action.PROCEED;
                });
            }

        }

        private void unregisterMouseCallbackInterceptors() {
            if (isHardwareAccelerated()) {
                browser.setCallbackInterceptor(MoveMouseWheel.Request.class,
                        (params) -> Action.PROCEED);
            }

        }

        private void hide(JPopupMenu popupMenu) {
            invokeLater(() -> {
                if (popupMenu.isShowing()) {
                    popupMenu.setVisible(false);
                }
            });
        }

        private boolean isHardwareAccelerated() {
            return browser.engine().options().renderingMode()
                    == RenderingMode.HARDWARE_ACCELERATED;
        }

        private class PopupMenuListenerImpl implements PopupMenuListener {

            private PopupMenuListenerImpl() {
            }

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                invokeLater(SwingContextMenu.this::notifyContextMenuClosed);
            }

            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        }
    }
}
