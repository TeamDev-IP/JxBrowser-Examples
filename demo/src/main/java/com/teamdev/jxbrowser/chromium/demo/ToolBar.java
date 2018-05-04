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

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.EditorCommand;
import com.teamdev.jxbrowser.chromium.SavePageType;
import com.teamdev.jxbrowser.chromium.demo.resources.Resources;
import com.teamdev.jxbrowser.chromium.events.Callback;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.events.ProvisionalLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.StartLoadingEvent;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 * @author TeamDev Ltd.
 */
public class ToolBar extends JPanel {
    private static final String RUN_JAVASCRIPT = "Run JavaScript...";
    private static final String CLOSE_JAVASCRIPT = "Close JavaScript Console";
    private static final String DEFAULT_URL = "about:blank";
    private final JTextField addressBar;
    private final BrowserView browserView;
    private JButton backwardButton;
    private JButton forwardButton;
    private JButton refreshButton;
    private JButton stopButton;
    private JMenuItem consoleMenuItem;

    public ToolBar(BrowserView browserView) {
        this.browserView = browserView;
        addressBar = createAddressBar();
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

    private static JButton createBackwardButton(final Browser browser) {
        return createButton("Back", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                browser.goBack();
            }
        });
    }

    private static JButton createForwardButton(final Browser browser) {
        return createButton("Forward", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                browser.goForward();
            }
        });
    }

    private static JButton createRefreshButton(final Browser browser) {
        return createButton("Refresh", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                browser.reload();
            }
        });
    }

    private static JButton createStopButton(final Browser browser) {
        return createButton("Stop", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                browser.stop();
            }
        });
    }

    private static JButton createButton(String caption, Action action) {
        ActionButton button = new ActionButton(caption, action);
        String imageName = caption.toLowerCase();
        button.setIcon(Resources.getIcon(imageName + ".png"));
        button.setRolloverIcon(Resources.getIcon(imageName + "-selected.png"));
        return button;
    }

    private static JCheckBoxMenuItem createCheckBoxMenuItem(String title, boolean selected,
            final CheckBoxMenuItemCallback action) {
        final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(title, selected);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                action.call(menuItem.isSelected());
            }
        });
        return menuItem;
    }

    public void didJSConsoleClose() {
        consoleMenuItem.setText(RUN_JAVASCRIPT);
    }

    private JPanel createActionsPane() {
        backwardButton = createBackwardButton(browserView.getBrowser());
        forwardButton = createForwardButton(browserView.getBrowser());
        refreshButton = createRefreshButton(browserView.getBrowser());
        stopButton = createStopButton(browserView.getBrowser());

        JPanel actionsPanel = new JPanel();
        actionsPanel.add(backwardButton);
        actionsPanel.add(forwardButton);
        actionsPanel.add(refreshButton);
        actionsPanel.add(stopButton);
        return actionsPanel;
    }

    private JTextField createAddressBar() {
        final JTextField result = new JTextField(DEFAULT_URL);
        result.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browserView.getBrowser().loadURL(result.getText());
            }
        });

        browserView.getBrowser().addLoadListener(new LoadAdapter() {
            @Override
            public void onStartLoadingFrame(StartLoadingEvent event) {
                if (event.isMainFrame()) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            refreshButton.setEnabled(false);
                            stopButton.setEnabled(true);
                        }
                    });
                }
            }

            @Override
            public void onProvisionalLoadingFrame(final ProvisionalLoadingEvent event) {
                if (event.isMainFrame()) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            result.setText(event.getURL());
                            result.setCaretPosition(result.getText().length());

                            Browser browser = event.getBrowser();
                            forwardButton.setEnabled(browser.canGoForward());
                            backwardButton.setEnabled(browser.canGoBack());
                        }
                    });
                }
            }

            @Override
            public void onFinishLoadingFrame(final FinishLoadingEvent event) {
                if (event.isMainFrame()) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            refreshButton.setEnabled(true);
                            stopButton.setEnabled(false);
                        }
                    });
                }
            }
        });
        return result;
    }

    private JComponent createMenuButton() {
        final JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(createConsoleMenuItem());
        popupMenu.add(createGetHTMLMenuItem());
        popupMenu.add(createPopupsMenuItem());
        popupMenu.add(createUploadFileMenuItem());
        popupMenu.add(createDownloadFileMenuItem());
        popupMenu.add(createJavaScriptDialogsMenuItem());
        popupMenu.add(createPDFViewerMenuItem());
        popupMenu.add(createFlashMenuItem());
        popupMenu.add(createGoogleMapsMenuItem());
        popupMenu.add(createHTML5VideoMenuItem());
        popupMenu.add(createZoomInMenuItem());
        popupMenu.add(createZoomOutMenuItem());
        popupMenu.add(createActualSizeMenuItem());
        popupMenu.add(createSaveWebPageMenuItem());
        popupMenu.add(createClearCacheMenuItem());
        popupMenu.add(createPreferencesSubMenu());
        popupMenu.add(createExecuteCommandSubMenu());
        popupMenu.add(createPrintMenuItem());
        popupMenu.addSeparator();
        popupMenu.add(createMoreMenuItem());
        popupMenu.addSeparator();
        popupMenu.add(createAboutMenuItem());

        final ActionButton button = new ActionButton("Preferences", null);
        button.setIcon(Resources.getIcon("gear.png"));
        button.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
                    popupMenu.show(e.getComponent(), 0, button.getHeight());
                } else {
                    popupMenu.setVisible(false);
                }
            }
        });
        return button;
    }

    private Component createPrintMenuItem() {
        JMenuItem menuItem = new JMenuItem("Print...");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browserView.getBrowser().print();
            }
        });
        return menuItem;
    }

    private Component createPreferencesSubMenu() {
        JMenu menu = new JMenu("Preferences");
        BrowserPreferences preferences = browserView.getBrowser().getPreferences();
        menu.add(createCheckBoxMenuItem("JavaScript Enabled", preferences.isJavaScriptEnabled(),
                new CheckBoxMenuItemCallback() {
                    public void call(boolean selected) {
                        BrowserPreferences preferences = browserView.getBrowser().getPreferences();
                        preferences.setJavaScriptEnabled(selected);
                        browserView.getBrowser().setPreferences(preferences);
                        browserView.getBrowser().reloadIgnoringCache();
                    }
                }));
        menu.add(createCheckBoxMenuItem("Images Enabled", preferences.isImagesEnabled(),
                new CheckBoxMenuItemCallback() {
                    public void call(boolean selected) {
                        BrowserPreferences preferences = browserView.getBrowser().getPreferences();
                        preferences.setImagesEnabled(selected);
                        browserView.getBrowser().setPreferences(preferences);
                        browserView.getBrowser().reloadIgnoringCache();
                    }
                }));
        menu.add(createCheckBoxMenuItem("Plugins Enabled", preferences.isPluginsEnabled(),
                new CheckBoxMenuItemCallback() {
                    public void call(boolean selected) {
                        BrowserPreferences preferences = browserView.getBrowser().getPreferences();
                        preferences.setPluginsEnabled(selected);
                        browserView.getBrowser().setPreferences(preferences);
                        browserView.getBrowser().reloadIgnoringCache();
                    }
                }));
        menu.add(createCheckBoxMenuItem("JavaScript Can Access Clipboard",
                preferences.isJavaScriptCanAccessClipboard(), new CheckBoxMenuItemCallback() {
                    public void call(boolean selected) {
                        BrowserPreferences preferences = browserView.getBrowser().getPreferences();
                        preferences.setJavaScriptCanAccessClipboard(selected);
                        browserView.getBrowser().setPreferences(preferences);
                        browserView.getBrowser().reloadIgnoringCache();
                    }
                }));
        menu.add(createCheckBoxMenuItem("JavaScript Can Open Windows",
                preferences.isJavaScriptCanOpenWindowsAutomatically(),
                new CheckBoxMenuItemCallback() {
                    public void call(boolean selected) {
                        BrowserPreferences preferences = browserView.getBrowser().getPreferences();
                        preferences.setJavaScriptCanOpenWindowsAutomatically(selected);
                        browserView.getBrowser().setPreferences(preferences);
                        browserView.getBrowser().reloadIgnoringCache();
                    }
                }));
        return menu;
    }

    private Component createClearCacheMenuItem() {
        JMenuItem menuItem = new JMenuItem("Clear Cache");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browserView.getBrowser().getCacheStorage().clearCache(new Callback() {
                    public void invoke() {
                        JOptionPane.showMessageDialog(browserView, "Cache is cleared successfully.",
                                "Clear Cache", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
            }
        });
        return menuItem;
    }

    private Component createExecuteCommandSubMenu() {
        final JMenu menu = new JMenu("Execute Command");
        menu.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
                Component[] menuItems = menu.getMenuComponents();
                for (Component menuItem : menuItems) {
                    menuItem.setEnabled(browserView.getBrowser()
                            .isCommandEnabled(((CommandMenuItem) menuItem).getCommand()));
                }
            }

            public void menuDeselected(MenuEvent e) {

            }

            public void menuCanceled(MenuEvent e) {

            }
        });

        menu.add(createExecuteCommandSubMenuItem("Cut", EditorCommand.CUT));
        menu.add(createExecuteCommandSubMenuItem("Copy", EditorCommand.COPY));
        menu.add(createExecuteCommandSubMenuItem("Paste", EditorCommand.PASTE));
        menu.add(createExecuteCommandSubMenuItem("Select All", EditorCommand.SELECT_ALL));
        menu.add(createExecuteCommandSubMenuItem("Unselect", EditorCommand.UNSELECT));
        menu.add(createExecuteCommandSubMenuItem("Undo", EditorCommand.UNDO));
        menu.add(createExecuteCommandSubMenuItem("Redo", EditorCommand.REDO));
        menu.add(createExecuteCommandSubMenuItem("Insert Text...", "Insert Text",
                EditorCommand.INSERT_TEXT));
        menu.add(createExecuteCommandSubMenuItem("Find Text...", "Find Text",
                EditorCommand.FIND_STRING));
        return menu;
    }

    private Component createExecuteCommandSubMenuItem(final String commandName,
            final EditorCommand command) {
        final CommandMenuItem menuItem = new CommandMenuItem(commandName, command);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browserView.getBrowser().executeCommand(command);
            }
        });
        return menuItem;
    }

    private Component createExecuteCommandSubMenuItem(final String commandName,
            final String dialogTitle, final EditorCommand command) {
        final CommandMenuItem menuItem = new CommandMenuItem(commandName, command);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String value = JOptionPane
                        .showInputDialog(browserView, "Command value:", dialogTitle,
                                JOptionPane.PLAIN_MESSAGE);
                browserView.getBrowser().executeCommand(command, value);
            }
        });
        return menuItem;
    }

    private Component createMoreMenuItem() {
        JMenuItem menuItem = new JMenuItem("More Features...");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browserView.getBrowser().loadURL(
                        "https://jxbrowser.support.teamdev.com/support/solutions/9000049010");
            }
        });
        return menuItem;
    }

    private Component createSaveWebPageMenuItem() {
        JMenuItem menuItem = new JMenuItem("Save Web Page...");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File("my-web-page.html"));
                int result = fileChooser.showSaveDialog(browserView);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String dirPath = new File(selectedFile.getParent(), "resources")
                            .getAbsolutePath();
                    browserView.getBrowser().saveWebPage(selectedFile.getAbsolutePath(), dirPath,
                            SavePageType.COMPLETE_HTML);
                }
            }
        });
        return menuItem;
    }

    private Component createActualSizeMenuItem() {
        JMenuItem menuItem = new JMenuItem("Actual Size");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browserView.getBrowser().zoomReset();
            }
        });
        return menuItem;
    }

    private Component createZoomOutMenuItem() {
        JMenuItem menuItem = new JMenuItem("Zoom Out");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browserView.getBrowser().zoomOut();
            }
        });
        return menuItem;
    }

    private Component createZoomInMenuItem() {
        JMenuItem menuItem = new JMenuItem("Zoom In");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browserView.getBrowser().zoomIn();
            }
        });
        return menuItem;
    }

    private Component createHTML5VideoMenuItem() {
        JMenuItem menuItem = new JMenuItem("HTML5 Video");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browserView.getBrowser()
                        .loadURL("http://www.w3.org/2010/05/video/mediaevents.html");
            }
        });
        return menuItem;
    }

    private Component createGoogleMapsMenuItem() {
        JMenuItem menuItem = new JMenuItem("Google Maps");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browserView.getBrowser().loadURL("https://maps.google.com/");
            }
        });
        return menuItem;
    }

    private Component createJavaScriptDialogsMenuItem() {
        JMenuItem menuItem = new JMenuItem("JavaScript Dialogs");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browserView.getBrowser().loadURL("http://www.javascripter.net/faq/alert.htm");
            }
        });
        return menuItem;
    }

    private Component createDownloadFileMenuItem() {
        JMenuItem menuItem = new JMenuItem("Download File");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browserView.getBrowser().loadURL(
                        "https://s3.amazonaws.com/cloud.teamdev.com/downloads/demo/jxbrowserdemo.jnlp");
            }
        });
        return menuItem;
    }

    private Component createGetHTMLMenuItem() {
        JMenuItem menuItem = new JMenuItem("Get HTML");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String html = browserView.getBrowser().getHTML();
                Window window = SwingUtilities.getWindowAncestor(browserView);
                JDialog dialog = new JDialog(window);
                dialog.setModal(true);
                dialog.setContentPane(new JScrollPane(new JTextArea(html)));
                dialog.setSize(700, 500);
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);

            }
        });
        return menuItem;
    }

    private JMenuItem createConsoleMenuItem() {
        consoleMenuItem = new JMenuItem(RUN_JAVASCRIPT);
        consoleMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (RUN_JAVASCRIPT.equals(consoleMenuItem.getText())) {
                    consoleMenuItem.setText(CLOSE_JAVASCRIPT);
                    firePropertyChange("JSConsoleDisplayed", false, true);
                } else {
                    consoleMenuItem.setText(RUN_JAVASCRIPT);
                    firePropertyChange("JSConsoleClosed", false, true);
                }
            }
        });
        return consoleMenuItem;
    }

    private JMenuItem createUploadFileMenuItem() {
        JMenuItem menuItem = new JMenuItem("Upload File");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browserView.getBrowser()
                        .loadURL("http://www.cs.tut.fi/~jkorpela/forms/file.html#example");
            }
        });
        return menuItem;
    }

    private JMenuItem createPopupsMenuItem() {
        JMenuItem menuItem = new JMenuItem("Popup Windows");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browserView.getBrowser().loadURL("http://www.popuptest.com");
            }
        });
        return menuItem;
    }

    private JMenuItem createPDFViewerMenuItem() {
        JMenuItem menuItem = new JMenuItem("PDF Viewer");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browserView.getBrowser().loadURL("http://www.orimi.com/pdf-test.pdf");
            }
        });
        return menuItem;
    }

    private JMenuItem createFlashMenuItem() {
        JMenuItem menuItem = new JMenuItem("Adobe Flash");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browserView.getBrowser().loadURL("http://helpx.adobe.com/flash-player.html");
            }
        });
        return menuItem;
    }

    private JMenuItem createAboutMenuItem() {
        JMenuItem menuItem = new JMenuItem("About JxBrowser Demo");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(ToolBar.this);
                AboutDialog aboutDialog = new AboutDialog(parentFrame);
                aboutDialog.setVisible(true);
            }
        });
        return menuItem;
    }

    private boolean isFocusRequired() {
        String url = addressBar.getText();
        return url.isEmpty() || url.equals(DEFAULT_URL);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (isFocusRequired()) {
                    addressBar.requestFocus();
                    addressBar.selectAll();
                }
            }
        });
    }

    private interface CheckBoxMenuItemCallback {
        void call(boolean selected);
    }

    private static class ActionButton extends JButton {
        private ActionButton(String hint, Action action) {
            super(action);
            setContentAreaFilled(false);
            setBorder(BorderFactory.createEmptyBorder());
            setBorderPainted(false);
            setRolloverEnabled(true);
            setToolTipText(hint);
            setText(null);
            setFocusable(false);
            setDefaultCapable(false);
        }
    }
}
