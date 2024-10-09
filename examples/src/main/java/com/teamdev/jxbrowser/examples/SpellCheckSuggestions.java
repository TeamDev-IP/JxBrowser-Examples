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

package com.teamdev.jxbrowser.examples;

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.browser.callback.ShowContextMenuCallback;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.menu.SpellCheckMenu;
import com.teamdev.jxbrowser.spellcheck.Dictionary;
import com.teamdev.jxbrowser.ui.Point;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * This example demonstrates how to configure spell checking functionality with the required
 * language and display the dictionary suggestions in the context menu that is displayed under a
 * misspelled word.
 */
public final class SpellCheckSuggestions {

    public static void main(String[] args) {
        // Enable heavyweight popup menu for the heavyweight
        // (default) BrowserView component. Otherwise – the popup
        // menu will be displayed under the BrowserView component.
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        Engine engine = Engine.newInstance(HARDWARE_ACCELERATED);
        Browser browser = engine.newBrowser();

        SwingUtilities.invokeLater(() -> {
            view = BrowserView.newInstance(browser);

            JFrame frame = new JFrame("Spell Check Suggestions");
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    engine.close();
                }
            });
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(view, BorderLayout.CENTER);
            frame.setSize(700, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        browser.set(ShowContextMenuCallback.class, (params, tell) -> {
            JPopupMenu popupMenu = new JPopupMenu();
            popupMenu.addPopupMenuListener(myPopupMenuListener(tell));

            // Add the suggestions menu items.
            SpellCheckMenu spellCheckMenu = params.spellCheckMenu();
            List<String> suggestions = spellCheckMenu.dictionarySuggestions();
            suggestions.forEach(suggestion -> {
                JMenuItem menuItem = new JMenuItem(suggestion);
                menuItem.addActionListener(e -> {
                    browser.replaceMisspelledWord(suggestion);
                    tell.close();
                });
                popupMenu.add(menuItem);
            });

            // Add menu separator if necessary.
            if (!suggestions.isEmpty()) {
                popupMenu.addSeparator();
            }

            if (!params.spellCheckMenu().misspelledWord().isEmpty()) {
                // Add the "Add to Dictionary" menu item.
                JMenuItem addToDictionary = new JMenuItem(
                        spellCheckMenu.addToDictionaryMenuItemText());
                addToDictionary.addActionListener(e -> {
                    Dictionary dictionary = engine.spellChecker().customDictionary();
                    dictionary.add(spellCheckMenu.misspelledWord());
                    tell.close();
                });
                popupMenu.add(addToDictionary);
            }


            // Display context menu at specified location.
            Point location = params.location();
            popupMenu.show(view, location.x(), location.y());
        });
        browser.mainFrame().ifPresent(mainFrame ->
                mainFrame.loadHtml("<html><body><textarea rows='20' cols='30'>" +
                        "Smple text with mitake.</textarea></body></html>"));

    }

    private static BrowserView view;

    private static PopupMenuListener myPopupMenuListener(ShowContextMenuCallback.Action tell) {
        return new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                tell.close();
            }
        };
    }
}
