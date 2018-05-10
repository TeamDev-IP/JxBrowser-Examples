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
import com.teamdev.jxbrowser.chromium.EditorCommand;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;

import static com.teamdev.jxbrowser.chromium.EditorCommand.COPY;
import static com.teamdev.jxbrowser.chromium.EditorCommand.CUT;
import static com.teamdev.jxbrowser.chromium.EditorCommand.FIND_STRING;
import static com.teamdev.jxbrowser.chromium.EditorCommand.INSERT_TEXT;
import static com.teamdev.jxbrowser.chromium.EditorCommand.PASTE;
import static com.teamdev.jxbrowser.chromium.EditorCommand.REDO;
import static com.teamdev.jxbrowser.chromium.EditorCommand.SELECT_ALL;
import static com.teamdev.jxbrowser.chromium.EditorCommand.UNDO;
import static com.teamdev.jxbrowser.chromium.EditorCommand.UNSELECT;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.JOptionPane.showInputDialog;

/**
 * The menu with some of the Edit commands supported by JxBrowser.
 *
 * @author Alexander Yevsyukov
 */
final class EditMenu {

    private final BrowserView browserView;
    private final Browser browser;
    private final JMenu menu;

    /**
     * Creates a new edit menu for the passed {@code BrowserView}.
     */
    EditMenu(BrowserView browserView) {
        this.browserView = browserView;
        this.browser = browserView.getBrowser();

        this.menu = new JMenu("Edit");

        menu.addMenuListener(new ItemEnabler());
        menu.add(createItem("Cut", CUT));
        menu.add(createItem("Copy", COPY));
        menu.add(createItem("Paste", PASTE));
        menu.add(createItem("Select All", SELECT_ALL));
        menu.add(createItem("Unselect", UNSELECT));
        menu.add(createItem("Undo", UNDO));
        menu.add(createItem("Redo", REDO));
        menu.add(createItem("Insert Text...", "Insert Text", INSERT_TEXT));
        menu.add(createItem("Find Text...", "Find Text", FIND_STRING));
    }

    /**
     * Obtains the UI component of the menu.
     */
    Component getComponent() {
        return this.menu;
    }

    /**
     * Creates a menu item for an editor command.
     */
    private Component createItem(String commandName, EditorCommand command) {
        final EditorCommandItem menuItem = new EditorCommandItem(commandName, command);
        menuItem.addActionListener(e -> browser.executeCommand(command));
        return menuItem;
    }

    /**
     * Creates a menu item for an editor command which requires a dialog input.
     */
    private Component createItem(String commandName, String dialogTitle, EditorCommand command) {
        final EditorCommandItem menuItem = new EditorCommandItem(commandName, command);
        menuItem.addActionListener(e -> {
            String value = showInputDialog(browserView, "", dialogTitle, PLAIN_MESSAGE);
            browser.executeCommand(command, value);
        });
        return menuItem;
    }

    /**
     * Enables or disables a menu item depending on the status of the corresponding command
     * in the {@code Browser}.
     */
    private class ItemEnabler implements MenuListener {

        public void menuSelected(MenuEvent e) {
            for (Component menuItem : menu.getMenuComponents()) {
                EditorCommand command = ((EditorCommandItem) menuItem).getCommand();
                menuItem.setEnabled(browser.isCommandEnabled(command));
            }
        }

        public void menuDeselected(MenuEvent e) {
        }

        public void menuCanceled(MenuEvent e) {
        }
    }
}
