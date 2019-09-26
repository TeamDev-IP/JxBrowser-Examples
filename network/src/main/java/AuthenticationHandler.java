/*
 *  Copyright 2019, TeamDev. All rights reserved.
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

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.net.callback.AuthenticateCallback;
import com.teamdev.jxbrowser.view.swing.BrowserView;

import javax.swing.*;
import java.awt.*;

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 * This example demonstrates how to show the Swing popup to fill in the credentials on the basic or digest authentication request.
 */
public class AuthenticationHandler {

    private static BrowserView view;

    public static void main(String[] args) {
        Engine engine = Engine.newInstance(EngineOptions.newBuilder(HARDWARE_ACCELERATED).build());
        engine.network().set(AuthenticateCallback.class, createAuthenticationPopup(view));
        Browser browser = engine.newBrowser();

        SwingUtilities.invokeLater(() -> {
            view = BrowserView.newInstance(browser);
            JFrame frame = new JFrame("Hello World");
            frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            frame.add(view, BorderLayout.CENTER);
            frame.setSize(800, 500);
            frame.setVisible(true);
        });

        browser.navigation().loadUrl("http://httpbin.org/basic-auth/user/passwd");
    }

    private static AuthenticateCallback createAuthenticationPopup(BrowserView view) {
        return (params, tell) -> SwingUtilities.invokeLater(() -> {
            JPanel userPanel = new JPanel();
            userPanel.setLayout(new GridLayout(2, 2));
            JLabel usernameLabel = new JLabel("Username:");
            JLabel passwordLabel = new JLabel("Password:");
            JTextField username = new JTextField();
            JPasswordField password = new JPasswordField();
            userPanel.add(usernameLabel);
            userPanel.add(username);
            userPanel.add(passwordLabel);
            userPanel.add(password);
            int input = JOptionPane.showConfirmDialog(view, userPanel, "Enter your password:",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (input == OK_OPTION) {
                tell.authenticate(username.getText(), new String(password.getPassword()));
            } else {
                tell.cancel();
            }
        });
    }
}
