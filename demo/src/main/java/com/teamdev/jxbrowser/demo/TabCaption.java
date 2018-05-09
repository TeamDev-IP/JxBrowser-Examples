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

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.teamdev.jxbrowser.demo.resources.Resources.loadIcon;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

final class TabCaption extends JPanel {

    private boolean selected;
    private TabCaptionComponent component;

    TabCaption() {
        setLayout(new BorderLayout());
        setOpaque(false);
        add(createComponent(), BorderLayout.CENTER);
        add(Box.createHorizontalStrut(1), BorderLayout.EAST);
    }

    private JComponent createComponent() {
        component = new TabCaptionComponent();
        component.addPropertyChangeListener(
            Tab.Event.CLOSE_BUTTON_PRESSED,
                evt -> firePropertyChange(Tab.Event.CLOSE_BUTTON_PRESSED,
                        evt.getOldValue(),
                        evt.getNewValue())
        );
        component.addPropertyChangeListener(
                Tab.Event.CLICKED,
                evt -> setSelected(true)
        );
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

    void setTitle(String title) {
        component.setTitle(title);
    }

    boolean isSelected() {
        return selected;
    }

    void setSelected(boolean selected) {
        boolean oldValue = this.selected;
        this.selected = selected;
        component.setSelected(selected);
        firePropertyChange("TabSelected", oldValue, selected);
    }

    private static class TabCaptionComponent extends JPanel {

        static final Color UNSELECTED_BACKGROUND = new Color(150, 150, 150);
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
                    int button = e.getButton();
                    if (button == MouseEvent.BUTTON1) {
                        firePropertyChange(Tab.Event.CLICKED, false, true);
                    }
                    if (button == MouseEvent.BUTTON2) {
                        firePropertyChange(Tab.Event.CLOSE_BUTTON_PRESSED, false, true);
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
            closeButton.setPressedIcon(loadIcon("close-pressed.png"));
            closeButton.setIcon(loadIcon("close.png"));
            closeButton.setContentAreaFilled(false);
            closeButton.setFocusable(false);
            closeButton.addActionListener(
                    e -> firePropertyChange(Tab.Event.CLOSE_BUTTON_PRESSED, false, true)
            );
            return closeButton;
        }

        void setTitle(final String title) {
            SwingUtilities.invokeLater(() -> {
                label.setText(title);
                label.setToolTipText(title);
            });
        }

        void setSelected(boolean selected) {
            setBackground(selected ? defaultBackground : UNSELECTED_BACKGROUND);
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
            final int width = getWidth();
            final int height = getHeight();
            final Color background = getBackground();
            g2d.setPaint(new GradientPaint(0, 0, Color.LIGHT_GRAY, 0, height, background));
            g2d.fillRect(0, 0, width, height);
            g2d.dispose();
            super.paint(g);
        }
    }
}
