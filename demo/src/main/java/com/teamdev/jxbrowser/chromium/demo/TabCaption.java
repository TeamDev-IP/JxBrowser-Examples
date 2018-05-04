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

package com.teamdev.jxbrowser.chromium.demo;

import com.teamdev.jxbrowser.chromium.demo.resources.Resources;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * @author TeamDev Ltd.
 */
public class TabCaption extends JPanel {

    private boolean selected;
    private TabCaptionComponent component;

    public TabCaption() {
        setLayout(new BorderLayout());
        setOpaque(false);
        add(createComponent(), BorderLayout.CENTER);
        add(Box.createHorizontalStrut(1), BorderLayout.EAST);
    }

    private JComponent createComponent() {
        component = new TabCaptionComponent();
        component.addPropertyChangeListener("CloseButtonPressed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                firePropertyChange("CloseButtonPressed", evt.getOldValue(), evt.getNewValue());
            }
        });
        component.addPropertyChangeListener("TabClicked", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                setSelected(true);
            }
        });
        return component;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(155, 26);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(50, 26);
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    public void setTitle(String title) {
        component.setTitle(title);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        boolean oldValue = this.selected;
        this.selected = selected;
        component.setSelected(selected);
        firePropertyChange("TabSelected", oldValue, selected);
    }

    private static class TabCaptionComponent extends JPanel {

        private final Color defaultBackground;
        private JLabel label;

        private TabCaptionComponent() {
            defaultBackground = getBackground();
            setLayout(new BorderLayout());
            setOpaque(false);
            add(createLabel(), BorderLayout.CENTER);
            add(createCloseButton(), BorderLayout.EAST);
        }

        private JComponent createLabel() {
            label = new JLabel();
            label.setOpaque(false);
            label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        firePropertyChange("TabClicked", false, true);
                    }
                    if (e.getButton() == MouseEvent.BUTTON2) {
                        firePropertyChange("CloseButtonPressed", false, true);
                    }
                }
            });
            return label;
        }

        private JComponent createCloseButton() {
            JButton closeButton = new JButton();
            closeButton.setOpaque(false);
            closeButton.setToolTipText("Close");
            closeButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            closeButton.setPressedIcon(Resources.getIcon("close-pressed.png"));
            closeButton.setIcon(Resources.getIcon("close.png"));
            closeButton.setContentAreaFilled(false);
            closeButton.setFocusable(false);
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    firePropertyChange("CloseButtonPressed", false, true);
                }
            });
            return closeButton;
        }

        public void setTitle(final String title) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    label.setText(title);
                    label.setToolTipText(title);
                }
            });
        }

        public void setSelected(boolean selected) {
            setBackground(selected ? defaultBackground : new Color(150, 150, 150));
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setPaint(
                    new GradientPaint(0, 0, Color.LIGHT_GRAY, 0, getHeight(), getBackground()));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
            super.paint(g);
        }
    }
}
