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
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

class AboutDialog extends JDialog {

    AboutDialog(Frame owner) {
        super(owner, "About JxBrowser Demo", true);
        initContent();
        initKeyStroke();
        setResizable(false);
        pack();
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void initContent() {
        JTextPane aboutText = new JTextPane();
        aboutText.setContentType("text/html");
        aboutText.setText("<html><font face='Arial' size='3'>" +
                "<font size='6'>JxBrowser Demo</font><br><br>" +
                "<b>Version " + ProductInfo.getVersion() + "</b><br><br>" +

                "This application is created for demonstration purposes only.<br>" +
                "&copy; 2017 TeamDev Ltd. All rights reserved.<br><br>" +

                "Powered by <a color='#3d82f8' href='https://www.teamdev.com/jxbrowser' " +
                "style='text-decoration:none'>JxBrowser</a>. See " +
                "<a color='#3d82f8' href='https://www.teamdev.com/jxbrowser-licence-agreement' " +
                "style='text-decoration:none'>terms of use.</a><br>" +

                "Based on <a color='#3d82f8' href='http://www.chromium.org/' " +
                "style='text-decoration:none'>Chromium project</a>. " +
                "See <a color='#3d82f8' " +
                "href='https://jxbrowser.support.teamdev.com/support/solutions/articles/9000033244' "
                +
                "style='text-decoration:none'>full list</a> of Chromium<br>components, " +
                "used in the current JxBrowser version.<br><br>" +

                "This demo uses WebKit and FFMpeg projects under LGPL.<br>" +

                "See licence text " +
                "<a color='#3d82f8' href='https://www.gnu.org/licenses/old-licenses/lgpl-2.0.html' "
                +
                "style='text-decoration:none'>LGPL v.2</a> and " +
                "<a color='#3d82f8' href='https://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html' "
                +
                "style='text-decoration:none'>LGPL v.2.1</a></font></html>");
        aboutText.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        aboutText.setEditable(false);
        aboutText.addHyperlinkListener(new HyperlinkListener() {
            @SuppressWarnings("ParameterNameDiffersFromOverriddenParameter")
            @Override
            public void hyperlinkUpdate(HyperlinkEvent event) {
                if (event.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    try {
                        Desktop desktop = java.awt.Desktop.getDesktop();
                        desktop.browse(event.getURL().toURI());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        add(aboutText, BorderLayout.CENTER);
    }

    private void initKeyStroke() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });
        JRootPane rootPane = getRootPane();
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "ESCAPE");
        rootPane.getActionMap().put("ESCAPE", new AbstractAction() {
            private static final long serialVersionUID = 6693635607417495802L;

            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
}
