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

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.ContextMenuHandler;
import com.teamdev.jxbrowser.chromium.ContextMenuParams;
import com.teamdev.jxbrowser.chromium.SpellCheckerService;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import java.awt.BorderLayout;
import java.awt.Point;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * The example demonstrates how to configure spell checking functionality
 * with the required language and display the dictionary suggestions
 * in the context menu that is displayed under a misspelled word.
 */
public class SpellCheckSuggestions {

    public static void main(String[] args) {
        // Enable heavyweight popup menu for the heavyweight
        // (default) BrowserView component. Otherwise – the popup
        // menu will be displayed under the BrowserView component.
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

        Browser browser = new Browser();
        BrowserView view = new BrowserView(browser);

        JFrame frame = new JFrame("JxBrowser – Spell Check Suggestions");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(view, BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        BrowserContext browserContext = browser.getContext();
        SpellCheckerService spellCheckerService =
                browserContext.getSpellCheckerService();

        // Enable spell checking.
        spellCheckerService.setEnabled(true);

        // Configure the dictionary language.
        spellCheckerService.setLanguage("en-US");

        // Register a custom context menu handler to display spell check suggestions.
        browser.setContextMenuHandler(new SwingContextMenuHandler(view, browser));

        browser.loadHTML("<html><body><textarea rows='20' cols='30'>" +
                "Smple text with mitake.</textarea></body></html>");
    }

    private static class SwingContextMenuHandler implements ContextMenuHandler {

        private final JComponent component;
        private final Browser browser;

        private SwingContextMenuHandler(JComponent parentComponent, Browser browser) {
            this.component = parentComponent;
            this.browser = browser;
        }

        private static JMenuItem createMenuItem(String title, final Runnable action) {
            JMenuItem result = new JMenuItem(title);
            result.addActionListener(e -> action.run());
            return result;
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
            // Add the suggestions menu items.
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
    }
}
