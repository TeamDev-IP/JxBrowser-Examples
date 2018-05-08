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

import java.awt.BorderLayout;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

final class TabbedPane extends JPanel {

    private final List<Tab> tabs;
    private final TabCaptions captions;
    private final JComponent contentContainer;

    TabbedPane() {
        this.captions = new TabCaptions();
        this.tabs = new ArrayList<>();
        this.contentContainer = new JPanel(new BorderLayout());

        setLayout(new BorderLayout());
        add(captions, BorderLayout.NORTH);
        add(contentContainer, BorderLayout.CENTER);
    }

    void disposeAllTabs() {
        for (Tab tab : getTabs()) {
            disposeTab(tab);
        }
    }

    private void disposeTab(Tab tab) {
        tab.getCaption().setSelected(false);
        tab.getContent().dispose();
        removeTab(tab);
        if (hasTabs()) {
            Tab firstTab = getFirstTab();
            firstTab.getCaption().setSelected(true);
        } else {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.setVisible(false);
                window.dispose();
            }
        }
    }

    private Tab findTab(TabCaption item) {
        for (Tab tab : getTabs()) {
            if (tab.getCaption().equals(item)) {
                return tab;
            }
        }
        return null;
    }

    void addTab(Tab tab) {
        TabCaption caption = tab.getCaption();
        caption.addPropertyChangeListener("CloseButtonPressed", new TabCaptionCloseTabListener());
        caption.addPropertyChangeListener("TabSelected", new SelectTabListener());

        TabContent content = tab.getContent();
        content.addPropertyChangeListener("TabClosed", new TabContentCloseTabListener());

        captions.addTab(caption);
        tabs.add(tab);
        validate();
        repaint();
    }

    private boolean hasTabs() {
        return !tabs.isEmpty();
    }

    private Tab getFirstTab() {
        return tabs.get(0);
    }

    private List<Tab> getTabs() {
        return new ArrayList<Tab>(tabs);
    }

    private void removeTab(Tab tab) {
        TabCaption tabCaption = tab.getCaption();
        captions.removeTab(tabCaption);
        tabs.remove(tab);
        validate();
        repaint();
    }

    void addTabButton(TabButton button) {
        captions.addTabButton(button);
    }

    void selectTab(Tab tab) {
        TabCaption tabCaption = tab.getCaption();
        TabCaption selectedTab = captions.getSelectedTab();
        if (selectedTab != null && !selectedTab.equals(tabCaption)) {
            selectedTab.setSelected(false);
        }
        captions.setSelectedTab(tabCaption);
    }

    private class TabCaptionCloseTabListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            TabCaption caption = (TabCaption) evt.getSource();
            Tab tab = findTab(caption);
            disposeTab(tab);
        }
    }

    private class SelectTabListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            TabCaption caption = (TabCaption) evt.getSource();
            Tab tab = findTab(caption);
            if (caption.isSelected()) {
                selectTab(tab);
            }
            if (!caption.isSelected()) {
                TabContent content = tab.getContent();
                contentContainer.remove(content);
                contentContainer.validate();
                contentContainer.repaint();
            } else {
                final TabContent content = tab.getContent();
                contentContainer.add(content, BorderLayout.CENTER);
                contentContainer.validate();
                contentContainer.repaint();
            }
        }
    }

    private class TabContentCloseTabListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            TabContent content = (TabContent) evt.getSource();
            Tab tab = findTab(content);
            disposeTab(tab);
        }

        private Tab findTab(TabContent content) {
            for (Tab tab : getTabs()) {
                if (tab.getContent().equals(content)) {
                    return tab;
                }
            }
            return null;
        }
    }
}
