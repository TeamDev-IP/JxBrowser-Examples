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

import java.awt.Color;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import static javax.swing.BoxLayout.*;

final class TabCaptions extends JPanel {

    private final JPanel tabs;
    private final JPanel buttons;

    private TabCaption selected;

    TabCaptions() {
        setLayout(new BoxLayout(this, X_AXIS));
        setBackground(Color.DARK_GRAY);

        this.tabs = createItemsPanel();
        add(tabs);
        this.buttons = createButtonsPanel();
        add(buttons);
        add(Box.createHorizontalGlue());
    }

    private JPanel createItemsPanel() {
        JPanel tabs = new JPanel();
        tabs.setOpaque(false);
        tabs.setLayout(new BoxLayout(tabs, X_AXIS));
        return tabs;
    }

    private JPanel createButtonsPanel() {
        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.setLayout(new BoxLayout(buttons, X_AXIS));
        return buttons;
    }

    void addTab(TabCaption item) {
        tabs.add(item);
    }

    void removeTab(TabCaption item) {
        tabs.remove(item);
    }

    void addButton(TabButton button) {
        buttons.add(button);
    }

    TabCaption getSelected() {
        return selected;
    }

    void select(TabCaption tab) {
        this.selected = tab;
        this.selected.setSelected(true);
    }
}
