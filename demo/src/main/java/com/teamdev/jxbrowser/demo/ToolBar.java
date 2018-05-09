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
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.events.ProvisionalLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.StartLoadingEvent;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

final class ToolBar extends JPanel {

    private final BrowserView browserView;
    private final Browser browser;

    private final JTextField addressBar;

    private JButton backwardButton;
    private JButton forwardButton;
    private JButton refreshButton;
    private JButton stopButton;

    private JMenuItem consoleMenuItem;

    ToolBar(BrowserView browserView) {
        this.browserView = browserView;
        this.browser = browserView.getBrowser();

        this.addressBar = createAddressBar();
        setLayout(new GridBagLayout());
        add(createActionsPane(),
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(addressBar, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(4, 0, 4, 5), 0, 0));
        add(createMenuButton(), new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.LINE_END, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5),
                0, 0));
    }

    //TODO:2018-05-08:alexander.yevsyukov: Can we have browser -> browser.goBack() instead?

    private static JButton createBackwardButton(final Browser browser) {
        return new ActionButton("Back", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                browser.goBack();
            }
        });
    }

    private static JButton createForwardButton(final Browser browser) {
        return new ActionButton("Forward", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                browser.goForward();
            }
        });
    }

    private static JButton createRefreshButton(final Browser browser) {
        return new ActionButton("Refresh", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                browser.reload();
            }
        });
    }

    private static JButton createStopButton(final Browser browser) {
        return new ActionButton("Stop", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                browser.stop();
            }
        });
    }

    private JPanel createActionsPane() {
        backwardButton = createBackwardButton(browser);
        forwardButton = createForwardButton(browser);
        refreshButton = createRefreshButton(browser);
        stopButton = createStopButton(browser);

        JPanel actionsPanel = new JPanel();
        actionsPanel.add(backwardButton);
        actionsPanel.add(forwardButton);
        actionsPanel.add(refreshButton);
        actionsPanel.add(stopButton);
        return actionsPanel;
    }

    private JTextField createAddressBar() {
        final JTextField result = new JTextField(Tab.DEFAULT_URL);
        result.addActionListener(e -> browser.loadURL(result.getText()));

        browser.addLoadListener(new LoadAdapter() {
            @Override
            public void onStartLoadingFrame(StartLoadingEvent event) {
                if (event.isMainFrame()) {
                    SwingUtilities.invokeLater(() -> {
                        refreshButton.setEnabled(false);
                        stopButton.setEnabled(true);
                    });
                }
            }

            @Override
            public void onProvisionalLoadingFrame(final ProvisionalLoadingEvent event) {
                if (event.isMainFrame()) {
                    SwingUtilities.invokeLater(() -> {
                        result.setText(event.getURL());
                        result.setCaretPosition(result.getText().length());

                        Browser browser = event.getBrowser();
                        forwardButton.setEnabled(browser.canGoForward());
                        backwardButton.setEnabled(browser.canGoBack());
                    });
                }
            }

            @Override
            public void onFinishLoadingFrame(final FinishLoadingEvent event) {
                if (event.isMainFrame()) {
                    SwingUtilities.invokeLater(() -> {
                        refreshButton.setEnabled(true);
                        stopButton.setEnabled(false);
                    });
                }
            }
        });
        return result;
    }

    void didJsConsoleClose() {
        consoleMenuItem.setText(Menu.RUN_JAVASCRIPT);
    }

    private JComponent createMenuButton() {
        Menu menu = new Menu(this, browserView);
        consoleMenuItem = menu.getConsoleMenuItem();
        return menu.getButton();
    }

    private boolean isFocusRequired() {
        String url = addressBar.getText();
        return url.isEmpty() || url.equals(Tab.DEFAULT_URL);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(() -> {
            if (isFocusRequired()) {
                addressBar.requestFocus();
                addressBar.selectAll();
            }
        });
    }
}
