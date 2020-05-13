package com.example.e4.rcp.parts;

import static org.eclipse.swt.layout.GridData.FILL;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.view.swt.BrowserView;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class SamplePart {

    @PostConstruct
    public void createComposite(Composite parent) {
        parent.setLayout(new GridLayout(1, false));

        Engine engine = Engine.newInstance(
                EngineOptions.newBuilder(RenderingMode.HARDWARE_ACCELERATED)
                        .licenseKey("your_license_key")
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