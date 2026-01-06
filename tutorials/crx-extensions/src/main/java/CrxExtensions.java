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

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static javax.swing.BoxLayout.X_AXIS;
import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.extensions.ExtensionAction;
import com.teamdev.jxbrowser.extensions.callback.OpenExtensionPopupCallback;
import com.teamdev.jxbrowser.extensions.event.ExtensionActionUpdated;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import com.teamdev.jxbrowser.view.swing.callback.DefaultOpenExtensionPopupCallback;
import com.teamdev.jxbrowser.view.swing.graphics.BitmapImage;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * This example demonstrates how to install Chrome extensions from CRX files and configure them.
 *
 * <p>In this example, we assume that necessary CRX files are bundled with
 * the application and put into the resources. After the installation, we display the extension icon
 * that will trigger the extension's action.
 *
 * @see <a
 * href="https://chromewebstore.google.com/detail/react-developer-tools/fmkadmapgofadopljbjfkapdkoienihi">React
 * DevTools extension</a>
 */
public final class CrxExtensions {

    private static final List<String> EXTENSION_FILES = List.of(
            "react_dev_tools.crx"
    );

    private static final Dimension BUTTON_SIZE = new Dimension(32, 32);

    @SuppressWarnings("resource") // The engine is closed when the Swing window is closed.
    public static void main(String[] args) {
        var options = EngineOptions
                .newBuilder(HARDWARE_ACCELERATED)
                .userDataDir(Paths.get("<path to the directory>"))
                .build();
        var engine = Engine.newInstance(options);
        var profile = engine.profiles().defaultProfile();
        var browser = profile.newBrowser();

        var extensions = profile.extensions();
        for (var crx : EXTENSION_FILES) {
            // If extension already exists, we will update it.
            var extension = extensions.install(getResourcePath(crx));

            // This line enables extension pop-ups, which are disabled by default. To modify
            // the appearance of the pop-up or interact with it automatically, create your own
            // implementation of `OpenExtensionPopupCallback`.
            extension.set(OpenExtensionPopupCallback.class,
                    new DefaultOpenExtensionPopupCallback());
        }

        invokeLater(() -> {
            var frame = new JFrame("CRX Chrome extension in JxBrowser");
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    engine.close();
                }
            });
            frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            frame.add(BrowserView.newInstance(browser), CENTER);
            frame.add(createExtensionBar(browser), NORTH);

            frame.setSize(1280, 900);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            browser.navigation().loadUrl("https://react.dev/");
        });
    }

    private static JPanel createExtensionBar(Browser browser) {
        var extensionBar = new JPanel();
        extensionBar.setLayout(new BoxLayout(extensionBar, X_AXIS));

        var openDevTools = new JButton("Open DevTools");
        openDevTools.addActionListener(e -> browser.devTools().show());
        extensionBar.add(openDevTools);

        var extensions = browser.profile().extensions();
        for (var extension : extensions.list()) {
            extension.action(browser).ifPresent(action -> {
                var button = new JButton();
                extensionBar.add(button);

                configureActionButton(button, action);
                action.on(ExtensionActionUpdated.class, params -> invokeLater(() -> {
                    configureActionButton(button, action);
                }));
            });
        }
        return extensionBar;
    }

    private static void configureActionButton(JButton button, ExtensionAction action) {
        var icon = BitmapImage.toToolkit(action.icon());
        var tooltip = action.tooltip();
        button.setIcon(new ImageIcon(icon));
        button.setText(tooltip);
        button.setEnabled(action.isEnabled());
        button.setPreferredSize(BUTTON_SIZE);
        if (button.getActionListeners().length == 0) {
            button.addActionListener(e -> action.click());
        }
    }

    private static Path getResourcePath(String name) {
        try {
            var resource = CrxExtensions.class.getClassLoader().getResource(name);
            if (resource != null) {
                return Paths.get(resource.toURI());
            } else {
                throw new IllegalStateException("Couldn't find the bundled extension.");
            }
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}
