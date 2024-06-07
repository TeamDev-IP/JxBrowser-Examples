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
import com.teamdev.jxbrowser.view.swing.BrowserView;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * This example demonstrates how to display "Select Client SSL Certificate"
 * dialog where an end-user must select an SSL certificate to continue loading
 * the web page.
 *
 * <p>Important: before you run this example, please follow the instruction at
 * <a href="https://badssl.com/download/">badssl.com</a> and install
 * the required custom SSL certificate.
 */
public final class SelectClientCertificate {

    private static BrowserView view;

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
            List<X509Certificate> certificates = params.certificates().stream()
                    .map(cerf -> cerf.toX509Certificate().orElse(null))
                    .filter(Objects::nonNull)
                    .toList();

            if (certificates.isEmpty()) {
                tell.cancel();
            }

            Object[] selectionValues = IntStream.range(0, certificates.size())
                    .mapToObj(i -> new SelectionValue(i, certificates.get(i)))
                    .toArray();

            Object selectedValue = JOptionPane.showInputDialog(
                    view,
                    params.message(), params.title(),
                    PLAIN_MESSAGE, null,
                    selectionValues, selectionValues[0]
            );

            if (selectedValue != null) {
                SelectionValue selected = (SelectionValue) selectedValue;
                tell.select(selected.index);
            }
        });

        browser.navigation().loadUrl("https://client.badssl.com/");
    }

    private record SelectionValue(int index, X509Certificate certificate) {

        @Override
        public String toString() {
            return certificate.getSubjectX500Principal().getName();
        }
    }
}
