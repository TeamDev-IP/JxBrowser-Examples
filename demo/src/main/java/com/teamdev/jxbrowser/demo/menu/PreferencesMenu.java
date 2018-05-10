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
final class PreferencesMenu {

    private final Browser browser;
    private final JMenu menu;

    PreferencesMenu(BrowserView browserView) {
        this.browser = browserView.getBrowser();

        this.menu = new JMenu("Preferences");
        BrowserPreferences p = browser.getPreferences();

        addItem("JavaScript Enabled",
                p.isJavaScriptEnabled(),
                new PreferencesSwitch() {
                    @Override
                    void set(boolean value) {
                        preferences().setJavaScriptEnabled(value);
                    }
                });

        addItem("Images Enabled",
                p.isImagesEnabled(),
                new PreferencesSwitch() {
                    @Override
                    void set(boolean value) {
                        preferences().setImagesEnabled(value);
                    }
                });

        addItem("Plugins Enabled",
                p.isPluginsEnabled(),
                new PreferencesSwitch() {
                    @Override
                    void set(boolean value) {
                        preferences().setPluginsEnabled(value);
                    }
                });

        addItem("JavaScript Can Access Clipboard",
                p.isJavaScriptCanAccessClipboard(),
                new PreferencesSwitch() {
                    @Override
                    void set(boolean value) {
                        preferences().setJavaScriptCanAccessClipboard(value);
                    }
                });
    }

    Component getComponent() {
        return this.menu;
    }

    private void addItem(String title, boolean currentState, PreferencesSwitch action) {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(title, currentState);
        item.addActionListener(e -> action.perform(item.isSelected()));
        menu.add(item);
    }

    /**
     * Abstract base for operations on {@code BrowserPreferences} that
     * set a passed value of a preference and then reload the {@code Browser}.
     */
    private abstract class PreferencesSwitch {

        /**
         * The current preferences set in a browser on which we perform the switch.
         */
        private BrowserPreferences preferences;

        /**
         * Sets preference item to the passed value.
         */
        abstract void set(boolean value);

        /**
         * Sets the value of a preference setting and reloads the browser.
         */
        void perform(boolean value) {
            preferences = browser.getPreferences();
            set(value);
            browser.setPreferences(preferences);
            browser.reloadIgnoringCache();
        }

        BrowserPreferences preferences() {
            return this.preferences;
        }
    }
}
