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
import com.teamdev.jxbrowser.chromium.CloseStatus;
import com.teamdev.jxbrowser.chromium.FileChooserMode;
import com.teamdev.jxbrowser.chromium.FileChooserParams;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import com.teamdev.jxbrowser.chromium.swing.DefaultDialogHandler;
import java.awt.BorderLayout;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * The example demonstrates how to register a custom DialogHandler and
 * override the functionality that displays file chooser dialog when
 * uploading a file using the INPUT TYPE="file" HTML element on a web page.
 */
public class FileUpload {

    public static void main(String[] args) {
        Browser browser = new Browser();
        final BrowserView view = new BrowserView(browser);

        JFrame frame = new JFrame("JxBrowser Example – File Upload");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(view, BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        browser.setDialogHandler(new DefaultDialogHandler(view) {
            @Override
            public CloseStatus onFileChooser(final FileChooserParams params) {
                AtomicReference<CloseStatus> result = new AtomicReference<>(CloseStatus.CANCEL);
                try {
                    // Display JFileChooser dialog in EDT and block current thread execution
                    // until the dialog is closed.
                    SwingUtilities.invokeAndWait(() -> {
                        if (params.getMode() == FileChooserMode.Open) {
                            JFileChooser fileChooser = new JFileChooser();
                            if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
                                File selectedFile = fileChooser.getSelectedFile();
                                params.setSelectedFiles(selectedFile.getAbsolutePath());
                                result.set(CloseStatus.OK);
                            }
                        }
                    });
                } catch (InterruptedException ignore) {
                    Thread.currentThread().interrupt();
                } catch (InvocationTargetException e) {
                    throw new IllegalStateException("Failed to display a dialog: ", e);
                }
                return result.get();
            }
        });
        browser.loadHTML("<html><body><input type='file'></body></html>");
    }
}