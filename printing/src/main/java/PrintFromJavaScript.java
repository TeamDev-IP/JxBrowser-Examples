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

import com.teamdev.jxbrowser.chromium.*;
import com.teamdev.jxbrowser.chromium.PrintJob;
import com.teamdev.jxbrowser.chromium.events.PrintJobEvent;
import com.teamdev.jxbrowser.chromium.events.PrintJobListener;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This example prints a <a href="http://www.javascriptkit.com/howto/newtech2.shtml">web page</a>,
 * which invokes the printing via JavaScript {@code window.print()} function.
 *
 * <p>Custom print settings are applied for:
 * <ol>
 *  <li>the landscape orientation;
 *  <li>printing background images.
 * </ol>
 */
public class PrintFromJavaScript {

    public static void main(String[] args) {
        final Browser browser = new Browser();
        BrowserView browserView = new BrowserView(browser);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JButton print = new JButton("Print");
        print.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browser.print();
            }
        });
        frame.add(print, BorderLayout.NORTH);
        frame.add(browserView, BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        browser.setPrintHandler(new PrintHandler() {
            @Override
            public PrintStatus onPrint(PrintJob printJob) {
                PrintSettings printSettings = printJob.getPrintSettings();
                printSettings.setLandscape(true);
                printSettings.setPrintBackgrounds(true);
                printJob.addPrintJobListener(new PrintJobListener() {
                    @Override
                    public void onPrintingDone(PrintJobEvent event) {
                        System.out.println("PrintingDone success: " + event.isSuccess());
                    }
                });
                return PrintStatus.CONTINUE;
            }
        });

        browser.loadURL("http://www.javascriptkit.com/howto/newtech2.shtml");
    }
}
