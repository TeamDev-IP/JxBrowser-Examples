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

import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.events.TitleEvent;
import com.teamdev.jxbrowser.chromium.events.TitleListener;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 * @author TeamDev Ltd.
 */
public class TabContent extends JPanel {

    private final BrowserView browserView;
    private final ToolBar toolBar;
    private final JComponent jsConsole;
    private final JComponent container;
    private final JComponent browserContainer;

    public TabContent(final BrowserView browserView) {
        this.browserView = browserView;
        this.browserView.getBrowser().addLoadListener(new LoadAdapter() {
            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent event) {
                if (event.isMainFrame()) {
                    firePropertyChange("PageTitleChanged", null,
                            TabContent.this.browserView.getBrowser().getTitle());
                }
            }
        });

        this.browserView.getBrowser().addTitleListener(new TitleListener() {
            @Override
            public void onTitleChange(TitleEvent event) {
                firePropertyChange("PageTitleChanged", null, event.getTitle());
            }
        });

        browserContainer = createBrowserContainer();
        jsConsole = createConsole();
        toolBar = createToolBar(browserView);

        container = new JPanel(new BorderLayout());
        container.add(browserContainer, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(container, BorderLayout.CENTER);
    }

    private ToolBar createToolBar(BrowserView browserView) {
        ToolBar toolBar = new ToolBar(browserView);
        toolBar.addPropertyChangeListener("TabClosed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                firePropertyChange("TabClosed", false, true);
            }
        });
        toolBar.addPropertyChangeListener("JSConsoleDisplayed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                showConsole();
            }
        });
        toolBar.addPropertyChangeListener("JSConsoleClosed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                hideConsole();
            }
        });
        return toolBar;
    }

    private void hideConsole() {
        showComponent(browserContainer);
    }

    private void showComponent(JComponent component) {
        container.removeAll();
        container.add(component, BorderLayout.CENTER);
        validate();
    }

    private void showConsole() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.add(browserContainer, JSplitPane.TOP);
        splitPane.add(jsConsole, JSplitPane.BOTTOM);
        splitPane.setResizeWeight(0.8);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        showComponent(splitPane);
    }

    private JComponent createConsole() {
        JSConsole result = new JSConsole(browserView.getBrowser());
        result.addPropertyChangeListener("JSConsoleClosed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                hideConsole();
                toolBar.didJSConsoleClose();
            }
        });
        return result;
    }

    private JComponent createBrowserContainer() {
        JPanel container = new JPanel(new BorderLayout());
        container.add(browserView, BorderLayout.CENTER);
        return container;
    }

    public void dispose() {
        browserView.getBrowser().dispose();
    }
}
