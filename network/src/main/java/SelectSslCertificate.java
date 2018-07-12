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

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.Certificate;
import com.teamdev.jxbrowser.chromium.CertificatesDialogParams;
import com.teamdev.jxbrowser.chromium.CloseStatus;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import com.teamdev.jxbrowser.chromium.swing.DefaultDialogHandler;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

/**
 * The example demonstrates how to display the "Select Client SSL Certificate"
 * dialog where you must select a required SSL certificate to continue loading web page.
 *
 * Important: before you run this example, please follow the instruction at
 * https://badssl.com/download/ and install the required custom SSL certificate.
 */
public class SelectSslCertificate {

    private static final String DIALOG_TITLE = "Select a certificate";
    private static final String DIALOG_MESSAGE =
            "Select a certificate to authenticate yourself to %s";

    public static void main(String[] args) {
        Browser browser = new Browser();
        final BrowserView view = new BrowserView(browser);

        JFrame frame = new JFrame("JxBrowser – Select Client SSL Certificate");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(view, BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        browser.setDialogHandler(new DefaultDialogHandler(view) {

            @Override
            public CloseStatus onSelectCertificate(CertificatesDialogParams params) {
                // Get a list of the installed client SSL certificates.
                List<Certificate> certificates = params.getCertificates();
                if (!certificates.isEmpty()) {
                    // Display a dialog where the user must select
                    // a client SSL certificate.
                    Object[] selectionValues = certificates.toArray();
                    Object selectedValue = JOptionPane.showInputDialog(
                            view, String.format(DIALOG_MESSAGE,
                                    params.getHostPortPair().getHostPort()),
                            DIALOG_TITLE, JOptionPane.PLAIN_MESSAGE,
                            null, selectionValues, selectionValues[0]);
                    if (selectedValue != null) {
                        // Tell the engine which SSL certificate has been selected.
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
