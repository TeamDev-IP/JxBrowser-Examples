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
import java.util.ArrayList;
import java.util.List;

/**
 * The example shows how to print a loaded web page
 * with custom print settings.
 */
public class PrintFromJava {

    public static void main(String[] args) {
        final Browser browser = new Browser();
        BrowserView view = new BrowserView(browser);

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
        frame.add(view, BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        browser.setPrintHandler(new PrintHandler() {
            @Override
            public PrintStatus onPrint(PrintJob printJob) {
                PrintSettings printSettings = printJob.getPrintSettings();
                printSettings.setPrinterName("Microsoft XPS Document Writer");
                printSettings.setLandscape(false);
                printSettings.setPrintBackgrounds(false);
                printSettings.setColorModel(ColorModel.COLOR);
                printSettings.setDuplexMode(DuplexMode.SIMPLEX);
                printSettings.setDisplayHeaderFooter(true);
                printSettings.setCopies(1);
                printSettings.setPaperSize(PaperSize.ISO_A4);

                List<PageRange> ranges = new ArrayList<PageRange>();
                ranges.add(new PageRange(0, 3));
                printSettings.setPageRanges(ranges);

                printJob.addPrintJobListener(new PrintJobListener() {
                    @Override
                    public void onPrintingDone(PrintJobEvent event) {
                        System.out.println("Printing is finished successfully: " +
                                event.isSuccess());
                    }
                });
                return PrintStatus.CONTINUE;
            }
        });

        browser.loadURL("http://www.teamdev.com/jxbrowser");
    }
}
