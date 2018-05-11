/*
 *  Copyright 2018, TeamDev. All rights reserved.
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

package com.teamdev.jxbrowser.demo.menu;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;

/**
 * The menu with some view-related operations in a browser.
 */
final class ViewMenu {

    private final BrowserView browserView;
    private final Browser browser;
    private final JMenu menu;

    ViewMenu(BrowserView browserView) {
        this.browserView = browserView;
        this.browser = browserView.getBrowser();

        this.menu = new JMenu("View");

        menu.add(viewPageSourceItem());
        menu.addSeparator();

        menu.add(createActualSizeMenuItem());
        menu.add(createZoomInMenuItem());
        menu.add(createZoomOutMenuItem());

    }

    Component getComponent() {
        return this.menu;
    }

    private Component viewPageSourceItem() {
        JMenuItem menuItem = new JMenuItem("Page Source");
        menuItem.addActionListener(e -> {
            String html = browser.getHTML();
            Window window = SwingUtilities.getWindowAncestor(browserView);
            JDialog dialog = new JDialog(window);
            dialog.setModal(true);
            dialog.setContentPane(new JScrollPane(new JTextArea(html)));
            dialog.setSize(700, 500);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);

        });
        return menuItem;
    }

    private Component createZoomInMenuItem() {
        JMenuItem menuItem = new JMenuItem("Zoom In");
        menuItem.addActionListener(e -> browser.zoomIn());
        return menuItem;
    }

    private Component createZoomOutMenuItem() {
        JMenuItem menuItem = new JMenuItem("Zoom Out");
        menuItem.addActionListener(e -> browser.zoomOut());
        return menuItem;
    }

    private Component createActualSizeMenuItem() {
        JMenuItem menuItem = new JMenuItem("Actual Size");
        menuItem.addActionListener(e -> browser.zoomReset());
        return menuItem;
    }
}
