/*
 *  Copyright 2024, TeamDev. All rights reserved.
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
import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.extensions.callback.InstallExtensionCallback;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.JFrame;

/**
 * Use this application to download a CRX file from Chrome Web Store.
 *
 * <p>To download a CRX file, launch the application, then find and install the
 * extension. Once the CRX files is downloaded, the application will close.
 */
public final class CrxFileFromChromeWebStore {

    private static final String EXTENSION_URL =
            "https://chromewebstore.google.com/detail/react-developer-tools/fmkadmapgofadopljbjfkapdkoienihi";

    @SuppressWarnings("resource") // The engine is closed when the Swing window is closed.
    public static void main(String[] args) {
        var engine = Engine.newInstance(HARDWARE_ACCELERATED);
        var browser = engine.newBrowser();

        var extensions = browser.profile().extensions();
        extensions.set(InstallExtensionCallback.class, (params, tell) -> {
            var name = params.extensionName();
            var source = Paths.get(params.extensionCrxFile());
            var target = Paths.get(name + ".crx");
            try {
                Files.copy(source, target);
                browser.mainFrame().ifPresent(frame -> {
                    var message = "Find downloaded CRX file at %s. You can close this window now."
                            .formatted(target.toAbsolutePath());
                    frame.loadHtml(message);
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            tell.cancel();
        });

        browser.navigation().loadUrl(EXTENSION_URL);

        invokeLater(() -> {
            var view = BrowserView.newInstance(browser);
            var frame = new JFrame("Chrome Web Store");
            frame.add(view);
            frame.setSize(1280, 900);
            frame.setLocationRelativeTo(null);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    engine.close();
                }
            });
            frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
