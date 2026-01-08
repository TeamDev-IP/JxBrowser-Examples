/*
 *  Copyright 2026, TeamDev. All rights reserved.
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

package com.teamdev.jxbrowser.demo.parts;

import static org.eclipse.swt.layout.GridData.FILL;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.logging.Logger;
import com.teamdev.jxbrowser.logging.Level;
import com.teamdev.jxbrowser.view.swt.BrowserView;

import jakarta.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class SamplePart {

    @PostConstruct
    public void createComposite(Composite parent) {
        // Uncomment to enable debug logging:
        // Logger.level(Level.DEBUG);
        parent.setLayout(new GridLayout(1, false));
        System.setProperty("jxbrowser.license.key", "<the license key>");
        Engine engine = Engine.newInstance(
                EngineOptions.newBuilder(RenderingMode.HARDWARE_ACCELERATED)
                        .build());
        Browser browser = engine.newBrowser();

        Text addressBar = new Text(parent, SWT.SINGLE);
        addressBar.setText("https://google.com");
        addressBar.addListener(SWT.Traverse, event -> {
            if (event.detail == SWT.TRAVERSE_RETURN) {
                browser.navigation().loadUrl(addressBar.getText());
            }
        });
        browser.navigation().loadUrl(addressBar.getText());

        GridData textGrid = new GridData();
        textGrid.horizontalAlignment = GridData.FILL;
        addressBar.setLayoutData(textGrid);

        BrowserView view = BrowserView.newInstance(parent, browser);
        view.setLayoutData(new GridData(FILL, FILL, true, true));
    }
}
