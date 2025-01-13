/*
 *  Copyright 2025, TeamDev. All rights reserved.
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
import static com.teamdev.jxbrowser.print.ColorModel.COLOR;
import static com.teamdev.jxbrowser.print.Orientation.PORTRAIT;
import static com.teamdev.jxbrowser.print.PaperSize.ISO_A4;

import com.teamdev.jxbrowser.browser.callback.PrintCallback;
import com.teamdev.jxbrowser.browser.callback.PrintHtmlCallback;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.frame.Frame;
import com.teamdev.jxbrowser.print.event.PrintCompleted;

/**
 * This example demonstrates how to configure print settings programmatically and print the
 * currently loaded web page on the default system printer.
 */
public final class PrintSettings {

    public static void main(String[] args) {
        var engine = Engine.newInstance(HARDWARE_ACCELERATED);
        var browser = engine.newBrowser();
        browser.set(PrintCallback.class, (params, tell) -> tell.print());
        // #docfragment "Callback"
        browser.set(PrintHtmlCallback.class, (params, tell) -> {
            // #docfragment "Configure settings"
            var printer = params.printers()
                    .defaultPrinter()
                    .orElseThrow(IllegalStateException::new);
            var printJob = printer.printJob();
            printJob.settings()
                    .header("<span style='font-size: 12px;'>Page header:</span>"
                            + "<span class='title'></span>")
                    .footer("<span style='font-size: 12px;'>Page footer:</span>"
                            + "<span class='pageNumber'></span>")
                    .paperSize(ISO_A4)
                    .colorModel(COLOR)
                    .enablePrintingBackgrounds()
                    .disablePrintingHeaderFooter()
                    .orientation(PORTRAIT)
                    .apply();
            // #enddocfragment "Configure settings"
            printJob.on(PrintCompleted.class, event -> {
                if (event.isSuccess()) {
                    System.out.println("Printing is completed successfully.");
                } else {
                    System.out.println("Printing has failed.");
                }
                engine.close();
            });
            // #docfragment "Proceed"
            tell.proceed(printer);
            // #enddocfragment "Proceed"
        });
        // #enddocfragment "Callback"
        browser.navigation().loadUrlAndWait("https://en.wikipedia.org/wiki/Printing");
        browser.mainFrame().ifPresent(Frame::print);
    }
}
