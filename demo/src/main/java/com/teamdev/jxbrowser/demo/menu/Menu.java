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
import com.teamdev.jxbrowser.demo.AboutDialog;
import com.teamdev.jxbrowser.demo.ActionButton;
import com.teamdev.jxbrowser.demo.ToolBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Application menu shown from a drop-down button.
 *
 * @author unascribed
 * @author Alexander Yevsyukov
 */
public final class Menu {

    public static final String RUN_JAVASCRIPT = "Run JavaScript...";
    private static final String CLOSE_JAVASCRIPT = "Close JavaScript Console";

    private final ToolBar toolbar;
    private final BrowserView browserView;
    private final Browser browser;
    private final ActionButton button;

    private JMenuItem consoleMenuItem;

    public Menu(ToolBar toolbar, BrowserView browserView) {
        this.toolbar = toolbar;
        this.browserView = browserView;
        this.browser = browserView.getBrowser();

        final JPopupMenu menu = new JPopupMenu();
        menu.add(createFileMenu());
        menu.add(createViewMenu());
        menu.add(createEditMenu());
        menu.add(createPreferencesSubMenu());
        menu.addSeparator();

        menu.add(createClearCacheMenuItem());
        menu.addSeparator();

        menu.add(createConsoleMenuItem(toolbar));
        menu.add(createPopupsMenuItem());
        menu.add(createJavaScriptDialogsMenuItem());
        menu.add(createPDFViewerMenuItem());
        menu.add(createFlashMenuItem());
        menu.add(createGoogleMapsMenuItem());
        menu.add(createHtml5VideoMenuItem());
        menu.addSeparator();

        menu.add(createMoreMenuItem());
        menu.addSeparator();

        menu.add(createAboutMenuItem());

        this.button = new ActionButton("Menu", null);
        button.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
                    menu.show(e.getComponent(), 0, button.getHeight());
                } else {
                    menu.setVisible(false);
                }
            }
        });
    }

    public JComponent getButton() {
        return button;
    }

    private Component createFileMenu() {
        FileMenu menu = new FileMenu(browserView);
        return menu.getComponent();
    }

    private Component createViewMenu() {
        ViewMenu menu = new ViewMenu(browserView);
        return menu.getComponent();
    }

    private Component createEditMenu() {
        EditMenu menu = new EditMenu(browserView);
        return menu.getComponent();
    }

    private Component createPreferencesSubMenu() {
        PreferencesMenu menu = new PreferencesMenu(browserView);
        return menu.getComponent();
    }

    private Component createClearCacheMenuItem() {
        JMenuItem menuItem = new JMenuItem("Clear Cache");
        menuItem.addActionListener(
                e -> browser.getCacheStorage().clearCache(
                        () -> showMessageDialog(browserView, "Cache is cleared successfully.",
                                "Clear Cache", INFORMATION_MESSAGE))
        );
        return menuItem;
    }

    private Component createMoreMenuItem() {
        JMenuItem menuItem = new JMenuItem("More Features...");
        menuItem.addActionListener(
                e -> browser.loadURL("https://jxbrowser-support.teamdev.com/docs/guides/")
        );
        return menuItem;
    }

    private Component createHtml5VideoMenuItem() {
        JMenuItem menuItem = new JMenuItem("HTML5 Video");
        menuItem.addActionListener(
                e -> browser.loadURL("http://www.w3.org/2010/05/video/mediaevents.html")
        );
        return menuItem;
    }

    private Component createGoogleMapsMenuItem() {
        JMenuItem menuItem = new JMenuItem("Google Maps");
        menuItem.addActionListener(e -> browser.loadURL("https://maps.google.com/"));
        return menuItem;
    }

    private Component createJavaScriptDialogsMenuItem() {
        JMenuItem menuItem = new JMenuItem("JavaScript Dialogs");
        menuItem.addActionListener(
                e -> browser.loadURL("http://www.javascripter.net/faq/alert.htm")
        );
        return menuItem;
    }

    private JMenuItem createConsoleMenuItem(final ToolBar toolbar) {
        consoleMenuItem = new JMenuItem(RUN_JAVASCRIPT);
        consoleMenuItem.addActionListener(e -> {
            if (RUN_JAVASCRIPT.equals(consoleMenuItem.getText())) {
                consoleMenuItem.setText(CLOSE_JAVASCRIPT);
                toolbar.firePropertyChange("JSConsoleDisplayed", false, true);
            } else {
                consoleMenuItem.setText(RUN_JAVASCRIPT);
                toolbar.firePropertyChange("JSConsoleClosed", false, true);
            }
        });
        return consoleMenuItem;
    }

    private JMenuItem createPopupsMenuItem() {
        JMenuItem menuItem = new JMenuItem("Popup Windows");
        menuItem.addActionListener(e -> browser.loadURL("http://www.popuptest.com"));
        return menuItem;
    }

    private JMenuItem createPDFViewerMenuItem() {
        JMenuItem menuItem = new JMenuItem("PDF Viewer");
        menuItem.addActionListener(e -> browser.loadURL("http://www.orimi.com/pdf-test.pdf"));
        return menuItem;
    }

    private JMenuItem createFlashMenuItem() {
        JMenuItem menuItem = new JMenuItem("Adobe Flash");
        menuItem.addActionListener(e -> browser.loadURL("http://helpx.adobe.com/flash-player.html"));
        return menuItem;
    }

    private JMenuItem createAboutMenuItem() {
        JMenuItem menuItem = new JMenuItem("About JxBrowser Demo");
        menuItem.addActionListener(e -> {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(toolbar);
            AboutDialog aboutDialog = new AboutDialog(parentFrame);
            aboutDialog.setVisible(true);
        });
        return menuItem;
    }


    public JMenuItem getConsoleMenuItem() {
        return consoleMenuItem;
    }
}
