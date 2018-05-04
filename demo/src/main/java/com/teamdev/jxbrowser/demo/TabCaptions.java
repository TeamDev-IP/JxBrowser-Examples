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
import javax.swing.JComponent;
import javax.swing.JPanel;

class TabCaptions extends JPanel {

    private TabCaption selectedTab;

    private JPanel tabsPane;
    private JPanel buttonsPane;

    TabCaptions() {
        createUI();
    }

    private void createUI() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(Color.DARK_GRAY);
        add(createItemsPane());
        add(createButtonsPane());
        add(Box.createHorizontalGlue());
    }

    private JComponent createItemsPane() {
        tabsPane = new JPanel();
        tabsPane.setOpaque(false);
        tabsPane.setLayout(new BoxLayout(tabsPane, BoxLayout.X_AXIS));
        return tabsPane;
    }

    private JComponent createButtonsPane() {
        buttonsPane = new JPanel();
        buttonsPane.setOpaque(false);
        buttonsPane.setLayout(new BoxLayout(buttonsPane, BoxLayout.X_AXIS));
        return buttonsPane;
    }

    void addTab(TabCaption item) {
        tabsPane.add(item);
    }

    void removeTab(TabCaption item) {
        tabsPane.remove(item);
    }

    void addTabButton(TabButton button) {
        buttonsPane.add(button);
    }

    TabCaption getSelectedTab() {
        return selectedTab;
    }

    void setSelectedTab(TabCaption selectedTab) {
        this.selectedTab = selectedTab;
        this.selectedTab.setSelected(true);
    }
}
