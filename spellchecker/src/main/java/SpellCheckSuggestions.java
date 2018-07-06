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

import com.teamdev.jxbrowser.chromium.*;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * The example demonstrates how to work with spellchecker API.
 */
public class SpellCheckSuggestions {
    public static void main(String[] args) {
        // Enable heavyweight popup menu for heavyweight (default) BrowserView component.
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

        Browser browser = new Browser();
        BrowserView view = new BrowserView(browser);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(view, BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        BrowserContext context = browser.getContext();
        SpellCheckerService spellCheckerService = context.getSpellCheckerService();
        // Enable SpellCheckSuggestions service.
        spellCheckerService.setEnabled(true);
        // Configure SpellCheckSuggestions's language.
        spellCheckerService.setLanguage("en-US");

        browser.setContextMenuHandler(new MyContextMenuHandler(view, browser));
        browser.loadHTML("<html><body><textarea rows='20' cols='30'>" +
                "Smple text with mitake.</textarea></body></html>");
    }

    private static class MyContextMenuHandler implements ContextMenuHandler {

        private final JComponent component;
        private final Browser browser;

        private MyContextMenuHandler(JComponent parentComponent, Browser browser) {
            this.component = parentComponent;
            this.browser = browser;
        }

        public void showContextMenu(final ContextMenuParams params) {
            SwingUtilities.invokeLater(() -> {
                JPopupMenu popupMenu = createPopupMenu(params);
                Point location = params.getLocation();
                popupMenu.show(component, location.x, location.y);
            });
        }

        private JPopupMenu createPopupMenu(final ContextMenuParams params) {
            final JPopupMenu result = new JPopupMenu();
            // Add suggestions menu items.
            List<String> suggestions = params.getDictionarySuggestions();
            for (final String suggestion : suggestions) {
                result.add(createMenuItem(suggestion, () ->
                        browser.replaceMisspelledWord(suggestion)));
            }
            if (!suggestions.isEmpty()) {
                // Add the "Add to Dictionary" menu item.
                result.addSeparator();
                result.add(createMenuItem("Add to Dictionary", () -> {
                    String misspelledWord = params.getMisspelledWord();
                    browser.addWordToSpellCheckerDictionary(misspelledWord);
                }));
            }
            return result;
        }

        private static JMenuItem createMenuItem(String title, final Runnable action) {
            JMenuItem result = new JMenuItem(title);
            result.addActionListener(e -> action.run());
            return result;
        }
    }
}
