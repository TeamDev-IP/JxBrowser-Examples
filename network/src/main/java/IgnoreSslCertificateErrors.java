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
import com.teamdev.jxbrowser.chromium.CertificateErrorParams;
import com.teamdev.jxbrowser.chromium.DefaultLoadHandler;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * The example demonstrates how to ignore SSL certificate errors and
 * continue loading a website with invalid SSL certificate.
 */
public class IgnoreSslCertificateErrors {

    public static void main(String[] args) {
        Browser browser = new Browser();
        BrowserView browserView = new BrowserView(browser);

        JFrame frame = new JFrame("JxBrowser - Ignore SSL Certificate Errors");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(browserView, BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        browser.setLoadHandler(new DefaultLoadHandler() {
            @Override
            public boolean onCertificateError(CertificateErrorParams params) {
                // Get the invalid SSL certificate details.
                Certificate certificate = params.getCertificate();
                System.out.println("Subject name: " + certificate.getSubjectName());
                System.out.println("Issuer name: " + certificate.getIssuerName());
                System.out.println("Certificate expired? " + certificate.hasExpired());

                // Get the NetError code (e.g. CERT_AUTHORITY_INVALID).
                System.out.println("Error code: " + params.getCertificateError());

                // Return false to ignore the SSL certificate error and
                // continue loading the web page.
                return false;
            }
        });
        // Load HTTPS website with invalid SSL certificate.
        browser.loadURL("https://self-signed.badssl.com/");
    }
}

