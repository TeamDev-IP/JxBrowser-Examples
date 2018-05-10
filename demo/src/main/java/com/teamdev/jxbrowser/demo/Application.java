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

import com.teamdev.jxbrowser.chromium.internal.Environment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static com.teamdev.jxbrowser.demo.resources.Resources.loadIcon;

public final class Application {

    private Application() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            throw new IllegalStateException("Unable to set look and feel", e);
        }
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
    }

    private void initAndDisplayUI() {
        final TabbedPane tabbedPane = new TabbedPane();
        insertTab(tabbedPane, TabFactory.createFirstTab());
        insertNewTabButton(tabbedPane);

        JFrame frame = new JFrame("JxBrowser Demo");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @SuppressWarnings("CallToSystemExit") // This is what we do on closing the window.
            @Override
            public void windowClosing(WindowEvent e) {
                tabbedPane.disposeAllTabs();
                if (Environment.isMac()) {
                    System.exit(0);
                }
            }
        });
        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setSize(1024, 700);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(loadIcon("jxbrowser16x16.png").getImage());
        frame.setVisible(true);
    }

    private static void insertNewTabButton(final TabbedPane tabbedPane) {
        TabButton button = new TabButton(loadIcon("new-tab.png"), "New tab");
        button.addActionListener(e -> insertTab(tabbedPane, TabFactory.createTab()));
        tabbedPane.addTabButton(button);
    }

    private static void insertTab(TabbedPane tabbedPane, Tab tab) {
        tabbedPane.addTab(tab);
        tabbedPane.selectTab(tab);
    }

    public static void main(String[] args) {
        Application app = new Application();
        SwingUtilities.invokeLater(app::initAndDisplayUI);
    }
}
