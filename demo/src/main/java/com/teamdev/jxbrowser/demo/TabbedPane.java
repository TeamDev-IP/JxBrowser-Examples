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

import com.google.common.collect.ImmutableList;

import java.awt.BorderLayout;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

        Optional<Tab> tabToSelect = getLastTab();
        if (tabToSelect.isPresent()) {
            Tab previous = tabToSelect.get();
            previous.getCaption().setSelected(true);
        } else {
            disposeWindow();
        }
    }

    private Optional<Tab> getLastTab() {
        if (!tabs.isEmpty()) {
            final Tab result = tabs.get(tabs.size() - 1);
            return Optional.of(result);
        }
        return Optional.empty();
    }

    private void disposeWindow() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.setVisible(false);
            window.dispose();
        }
    }

    private Tab findTab(TabCaption item) {
        for (Tab tab : getTabs()) {
            if (tab.getCaption().equals(item)) {
                return tab;
            }
        }
        throw new IllegalStateException("Unable to find tab with caption: " + item);
    }

    private List<Tab> getTabs() {
        return ImmutableList.copyOf(tabs);
    }

    private void removeTab(Tab tab) {
        TabCaption tabCaption = tab.getCaption();
        captions.removeTab(tabCaption);
        tabs.remove(tab);
        validate();
        repaint();
    }

    void addTabButton(TabButton button) {
        captions.addButton(button);
    }

    void selectTab(Tab tab) {
        TabCaption tabCaption = tab.getCaption();
        TabCaption selectedTab = captions.getSelected();
        if (selectedTab != null && !selectedTab.equals(tabCaption)) {
            selectedTab.setSelected(false);
        }
        captions.select(tabCaption);
    }

    void addTab(Tab tab) {
        TabCaption caption = createCaption(tab);
        setCloseListener(tab);

        captions.addTab(caption);
        tabs.add(tab);
        validate();
        repaint();
    }

    private TabCaption createCaption(Tab tab) {
        TabCaption caption = tab.getCaption();
        caption.addPropertyChangeListener(
            Tab.Event.CLOSE_BUTTON_PRESSED,
                evt -> {
                    TabCaption c = (TabCaption) evt.getSource();
                    Tab tabToClose = findTab(c);
                    disposeTab(tabToClose);
                }
        );
        caption.addPropertyChangeListener(Tab.Event.SELECTED, new SelectTabListener());
        return caption;
    }

    private void setCloseListener(Tab tab) {
        TabContent content = tab.getContent();
        content.addPropertyChangeListener(Tab.Event.CLOSED, new TabContentCloseListener());
    }

    private class SelectTabListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            TabCaption caption = (TabCaption) evt.getSource();
            Tab tab = findTab(caption);
            if (caption.isSelected()) {
                selectTab(tab);
            }
            TabContent content = tab.getContent();
            if (!caption.isSelected()) {
                contentContainer.remove(content);
            } else {
                contentContainer.add(content, BorderLayout.CENTER);
            }
            contentContainer.validate();
            contentContainer.repaint();
        }
    }

    private class TabContentCloseListener implements PropertyChangeListener {

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
            throw new IllegalStateException("Unable to find tab with content: " + content);
        }
    }
}
