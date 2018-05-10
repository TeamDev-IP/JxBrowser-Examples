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

import com.teamdev.jxbrowser.chromium.ProductInfo;
import com.teamdev.jxbrowser.demo.resources.Resources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.Year;

import static java.awt.event.KeyEvent.VK_ESCAPE;
import static javax.swing.BorderFactory.createEmptyBorder;
import static javax.swing.KeyStroke.getKeyStroke;
import static javax.swing.event.HyperlinkEvent.EventType.ACTIVATED;

/**
 * Displays copyright and licensing information.
 */
public final class AboutDialog extends JDialog {

    /**
     * Creates a new instance of the dialog associated with the passed frame.
     */
    public AboutDialog(Frame owner) {
        super(owner, "About JxBrowser Demo", true);
        initContent();
        initKeyStroke();
        setResizable(false);
        pack();
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initContent() {
        JTextPane aboutText = new JTextPane();
        aboutText.setContentType("text/html");
        String aboutHtml = loadHtml();
        aboutText.setText(aboutHtml);

        aboutText.setBorder(createEmptyBorder(20, 20, 20, 20));
        aboutText.setEditable(false);
        aboutText.addHyperlinkListener(event -> {
            if (event.getEventType().equals(ACTIVATED)) {
                try {
                    Desktop desktop = Desktop.getDesktop();
                    desktop.browse(event.getURL().toURI());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        add(aboutText, BorderLayout.CENTER);
    }

    /**
     * Loads the content from resources and puts current product version and
     * year into copyright statement.
     */
    private String loadHtml() {
        String fmtString = Resources.load("about.html");
        return String.format(fmtString, ProductInfo.getVersion(), Year.now());
    }

    private void initKeyStroke() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == VK_ESCAPE) {
                    dispose();
                }
            }
        });
        JRootPane rootPane = getRootPane();
        KeyStroke keyStroke = getKeyStroke(VK_ESCAPE, 0, false);
        final String actionKey = "ESCAPE";
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, actionKey);
        rootPane.getActionMap().put(actionKey, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
}
