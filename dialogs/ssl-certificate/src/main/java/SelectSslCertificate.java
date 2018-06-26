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

import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.JOptionPane.showInputDialog;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.Certificate;
import com.teamdev.jxbrowser.chromium.CertificatesDialogParams;
import com.teamdev.jxbrowser.chromium.CloseStatus;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import com.teamdev.jxbrowser.chromium.swing.DefaultDialogHandler;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * The example demonstrates how to display the Select Client SSL Certificate dialog where
 * the user must select a required SSL certificate to continue loading web page.
 *
 * This example uses https://badssl.com/ to demonstrate this functionality. Before you run
 * the example, follow the instruction at https://badssl.com/download/ and install
 * the required client SSL certification. Once you install the certificate, run this example.
 */
public class SelectSslCertificate {

    public static void main(String[] args) {
        Browser browser = new Browser();
        final BrowserView view = new BrowserView(browser);

        JFrame frame = new JFrame("JxBrowser Example – Select Client SSL Certificate");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(view, BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        browser.setDialogHandler(new DefaultDialogHandler(view) {
            @Override
            public CloseStatus onSelectCertificate(CertificatesDialogParams params) {
                List<Certificate> certificates = params.getCertificates();
                if (!certificates.isEmpty()) {
                    String message = "Select a certificate to authenticate yourself to " +
                            params.getHostPortPair().getHostPort();
                    Object[] selectionValues = certificates.toArray();
                    Object selectedValue = showInputDialog(
                            view, message, "Select a certificate",
                            PLAIN_MESSAGE, null, selectionValues,
                            selectionValues[0]);
                    if (selectedValue != null) {
                        params.setSelectedCertificate((Certificate) selectedValue);
                        return CloseStatus.OK;
                    }
                }
                return CloseStatus.CANCEL;
            }
        });
        browser.loadURL("https://client.badssl.com/");
    }
}