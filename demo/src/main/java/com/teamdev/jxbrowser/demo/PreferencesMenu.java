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

package com.teamdev.jxbrowser.demo;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;

/**
 * A menu with switches for tuning preferences used by a {@code Browser}.
 *
 * @author unascribed
 * @author Alexander Yevsyukov
 */
class PreferencesMenu {

    private final Browser browser;
    private final JMenu menu;

    PreferencesMenu(BrowserView browserView) {
        this.browser = browserView.getBrowser();

        this.menu = new JMenu("Preferences");
        BrowserPreferences preferences = browser.getPreferences();
        //TODO:2018-05-08:alexander.yevsyukov: Refactor to have
        // browser.getPreferences()
        // browser.setPreferences()
        // browser.reloadIgnoringCache()
        // called in the common base.
        // The callback should only define the "meat" of the call. For example something like:
        // selected -> { BrowserPreferences::setJavaScriptEnabled(selected) }
        menu.add(checkBoxItem("JavaScript Enabled", preferences.isJavaScriptEnabled(),
                selected -> {
                    BrowserPreferences newPrefs = browser.getPreferences();
                    newPrefs.setJavaScriptEnabled(selected);
                    browser.setPreferences(newPrefs);
                    browser.reloadIgnoringCache();
                }));
        menu.add(checkBoxItem("Images Enabled", preferences.isImagesEnabled(),
                selected -> {
                    BrowserPreferences preferences12 = browser.getPreferences();
                    preferences12.setImagesEnabled(selected);
                    browser.setPreferences(preferences12);
                    browser.reloadIgnoringCache();
                }));
        menu.add(checkBoxItem("Plugins Enabled", preferences.isPluginsEnabled(),
                selected -> {
                    BrowserPreferences preferences13 = browser.getPreferences();
                    preferences13.setPluginsEnabled(selected);
                    browser.setPreferences(preferences13);
                    browser.reloadIgnoringCache();
                }));
        menu.add(checkBoxItem("JavaScript Can Access Clipboard",
                preferences.isJavaScriptCanAccessClipboard(),
                selected -> {
                    BrowserPreferences preferences14 = browser.getPreferences();
                    preferences14.setJavaScriptCanAccessClipboard(selected);
                    browser.setPreferences(preferences14);
                    browser.reloadIgnoringCache();
                }));
    }

    Component getComponent() {
        return this.menu;
    }

    private static JCheckBoxMenuItem checkBoxItem(String title,
                                                  boolean selected,
                                                  CheckBoxMenuItemCallback action) {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(title, selected);
        item.addActionListener(e -> action.call(item.isSelected()));
        return item;
    }

    interface CheckBoxMenuItemCallback {
        void call(boolean selected);
    }
}
