/*
 *  Copyright 2023, TeamDev. All rights reserved.
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

package com.teamdev.jxbrowser.examples;

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.browser.callback.SelectClientCertificateCallback;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.net.tls.Certificate;
import com.teamdev.jxbrowser.net.tls.ClientCertificate;
import com.teamdev.jxbrowser.net.tls.SslPrivateKey;
import com.teamdev.jxbrowser.net.tls.X509Certificates;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * This example demonstrates how to display the "Select Client SSL Certificate" dialog where you
 * must select a required SSL certificate to continue loading web page.
 *
 * <p>Important: before you run this example, please follow the instruction at
 * <a href="https://badssl.com/download/">https://badssl.com/download/</a>
 * and install the required custom SSL certificate.
 */
public final class SelectClientCertificate {

    private static final String DIALOG_TITLE = "Select a certificate";
    private static final String DIALOG_MESSAGE =
            "Select a certificate to authenticate yourself to %s";

    public static void main(String[] args) {
        Engine engine = Engine.newInstance(HARDWARE_ACCELERATED);
        Browser browser = engine.newBrowser();

        SwingUtilities.invokeLater(() -> {
            view = BrowserView.newInstance(browser);

            JFrame frame = new JFrame("Select Client SSL Certificate");
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    engine.close();
                }
            });
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(view, BorderLayout.CENTER);
            frame.setSize(700, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        browser.set(SelectClientCertificateCallback.class, (params, tell) -> {
            if (params.certificates().size() != 0) {
                List<Certificate> certificates = params.certificates();
                List<X509Certificate> x509Certificates = new ArrayList<>();
                for (Certificate certificate : certificates) {
                    try {
                        x509Certificates.add(X509Certificates.of(
                                new ByteArrayInputStream(certificate.derEncodedValue())));
                    } catch (CertificateException e) {
                        e.printStackTrace();
                    }
                }
                Object[] selectionValues = x509Certificates.toArray();
                Object selectedValue = JOptionPane
                        .showInputDialog(view, String.format(DIALOG_MESSAGE,
                                params.hostPort().host() + ":" + params.hostPort().port()),
                                DIALOG_TITLE, PLAIN_MESSAGE, null, selectionValues,
                                selectionValues[0]);
                if (selectedValue != null) {
                    // Tell the engine which SSL certificate has been selected.
                    try {
                        X509Certificate certificate = (X509Certificate) selectedValue;
                        ClientCertificate clientCertificate = ClientCertificate.of(
                                certificate, SslPrivateKey.of(certificate.getEncoded()));
                        // TODO:2024-04-10:yevhenii.nadtochii: Not working.
                        //  Should be removed?
                        tell.select(clientCertificate);
                        return;
                    } catch (CertificateEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
            tell.cancel();
        });

        browser.navigation().loadUrl("https://client.badssl.com/");
    }

    private static BrowserView view;
}
