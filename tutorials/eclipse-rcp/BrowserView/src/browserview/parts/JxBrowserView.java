package browserview.parts;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.view.swt.BrowserView;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import javax.annotation.PostConstruct;

public class JxBrowserView {
    private final String LICENSE_KEY = "INSERT YOUR LICENSE KEY HERE";
    private Text addressBar;

    @PostConstruct
    public void createPartControl(Composite parent) {
        initialize(parent);
    }

    @Focus
    public void setFocus() {
        addressBar.setFocus();
    }

    private void initialize(Composite parent) {
        parent.setLayout(new GridLayout(1, false));

        Engine engine = createEngine();
        Browser browser = engine.newBrowser();

        addressBar = new Text(parent, SWT.SINGLE);
        addressBar.setText("https://google.com");
        addressBar.addListener(SWT.Traverse, event -> {
            if (event.detail == SWT.TRAVERSE_RETURN) {
                browser.navigation()
                       .loadUrl(addressBar.getText());
            }
        });

        browser.navigation()
               .loadUrl(addressBar.getText());

        GridData textGrid = new GridData();
        textGrid.horizontalAlignment = GridData.FILL;
        addressBar.setLayoutData(textGrid);

        BrowserView view = BrowserView.newInstance(parent, browser);
        view.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
    }

    private Engine createEngine() {
        return Engine.newInstance(EngineOptions.newBuilder(RenderingMode.OFF_SCREEN)
                                               .licenseKey(LICENSE_KEY)
                                               .addSwitch("--disable-direct-composition")
                                               .build());
    }
}
