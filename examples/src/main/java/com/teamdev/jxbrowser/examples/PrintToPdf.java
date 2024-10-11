/*
 *  Copyright 2024, TeamDev. All rights reserved.
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

import static com.teamdev.jxbrowser.engine.RenderingMode.OFF_SCREEN;
import static com.teamdev.jxbrowser.print.Orientation.PORTRAIT;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.browser.callback.PrintCallback;
import com.teamdev.jxbrowser.browser.callback.PrintHtmlCallback;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.frame.Frame;
import com.teamdev.jxbrowser.print.PdfPrinter;
import com.teamdev.jxbrowser.print.PdfPrinter.HtmlSettings;
import com.teamdev.jxbrowser.print.PrintJob;
import com.teamdev.jxbrowser.print.event.PrintCompleted;
import java.nio.file.Paths;

/**
 * This example demonstrates how to configure print settings programmatically and print the
 * currently loaded web page using the built-in PDF printer. In general, it shows how to save the
 * currently loaded web page as a PDF document.
 */
public final class PrintToPdf {

    public static void main(String[] args) {
        Engine engine = Engine.newInstance(OFF_SCREEN);
        Browser browser = engine.newBrowser();
        browser.set(PrintCallback.class, (params, tell) -> tell.print());
        browser.set(PrintHtmlCallback.class, (params, tell) -> {
            PdfPrinter<PdfPrinter.HtmlSettings> pdfPrinter =
                    params.printers().pdfPrinter();
            PrintJob<HtmlSettings> printJob = pdfPrinter.printJob();
            printJob.settings()
                    .pdfFilePath(Paths.get("dynamic-cubemap.pdf").toAbsolutePath())
                    .enablePrintingBackgrounds()
                    .orientation(PORTRAIT)
                    .apply();
            printJob.on(PrintCompleted.class, event -> {
                if (event.isSuccess()) {
                    System.out.println("Printing is completed successfully.");
                } else {
                    System.out.println("Printing has failed.");
                }
                engine.close();
            });
            tell.proceed(pdfPrinter);
        });
        browser.navigation()
                .loadUrlAndWait("https://webglsamples.org/dynamic-cubemap/dynamic-cubemap.html");
        browser.mainFrame().ifPresent(Frame::print);
    }
}
