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

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.demo.resources.Resources;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * @author TeamDev Ltd.
 */
class JsConsole extends JPanel {

    private static final String NEW_LINE = "\n";
    private static final String QUERY_LINE_START = ">> ";
    private final Browser browser;
    private final ExecutorService executor;
    private JTextArea console;

    public JsConsole(Browser browser) {
        this.browser = browser;
        this.executor = Executors.newCachedThreadPool();
        setLayout(new BorderLayout());
        add(createTitle(), BorderLayout.NORTH);
        add(createConsoleOutput(), BorderLayout.CENTER);
        add(createConsoleInput(), BorderLayout.SOUTH);
    }

    private static JComponent createTitleLabel() {
        return new JLabel("JavaScript Console");
    }

    private JComponent createConsoleInput() {
        JPanel result = new JPanel(new BorderLayout());
        result.setBackground(Color.WHITE);

        JLabel label = new JLabel(QUERY_LINE_START);
        label.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 0));

        final JTextField consoleInput = new JTextField();
        consoleInput.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        consoleInput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                executor.submit(new Runnable() {
                    public void run() {
                        final String script = consoleInput.getText();
                        JSValue jsValue = browser.executeJavaScriptAndReturnValue(script);
                        final String executionResult = jsValue.toString();
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                updateConsoleOutput(script, executionResult);
                                consoleInput.setText("");
                            }
                        });
                    }
                });
            }
        });
        result.add(label, BorderLayout.WEST);
        result.add(consoleInput, BorderLayout.CENTER);
        return result;
    }

    private JComponent createConsoleOutput() {
        console = new JTextArea();
        console.setFont(new Font("Consolas", Font.PLAIN, 12));
        console.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        console.setEditable(false);
        console.setWrapStyleWord(true);
        console.setLineWrap(true);
        console.setText("");
        JScrollPane scrollPane = new JScrollPane(console);
        scrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        return scrollPane;
    }

    private JComponent createTitle() {
        JPanel panel = new JPanel(new BorderLayout());
//        panel.setBackground(new Color(182, 191, 207));
        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        panel.add(createTitleLabel(), BorderLayout.WEST);
        panel.add(createCloseButton(), BorderLayout.EAST);
        return panel;
    }

    private JComponent createCloseButton() {
        JButton closeButton = new JButton();
        closeButton.setOpaque(false);
        closeButton.setToolTipText("Close JavaScript Console");
        closeButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        closeButton.setPressedIcon(Resources.getIcon("close-pressed.png"));
        closeButton.setIcon(Resources.getIcon("close.png"));
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusable(false);
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                firePropertyChange("JSConsoleClosed", false, true);
            }
        });
        return closeButton;
    }

    private void updateConsoleOutput(String script, String executionResult) {
        displayScript(script);
        displayExecutionResult(executionResult);
        console.setCaretPosition(console.getText().length());
    }

    private void displayExecutionResult(String result) {
        console.append(result);
        console.append(NEW_LINE);
    }

    private void displayScript(String script) {
        console.append(QUERY_LINE_START);
        console.append(script);
        console.append(NEW_LINE);
    }
}
