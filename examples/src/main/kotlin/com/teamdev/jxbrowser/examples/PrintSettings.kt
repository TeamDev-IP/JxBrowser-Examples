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

package com.teamdev.jxbrowser.examples

import com.teamdev.jxbrowser.browser.callback.PrintCallback
import com.teamdev.jxbrowser.browser.callback.PrintHtmlCallback
import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.dsl.browser.mainFrame
import com.teamdev.jxbrowser.dsl.browser.navigation
import com.teamdev.jxbrowser.dsl.callback.default
import com.teamdev.jxbrowser.dsl.callback.printers
import com.teamdev.jxbrowser.dsl.printer.job
import com.teamdev.jxbrowser.dsl.register
import com.teamdev.jxbrowser.dsl.subscribe
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.print.ColorModel
import com.teamdev.jxbrowser.print.Orientation
import com.teamdev.jxbrowser.print.PaperSize
import com.teamdev.jxbrowser.print.event.PrintCompleted

/**
 * This example demonstrates how to print the currently loaded web page
 * via the default system printer.
 */
fun main() {
    val engine = Engine(RenderingMode.OFF_SCREEN)
    val browser = engine.newBrowser()

    browser.register(PrintCallback { _, tell -> tell.print() })

    browser.register(PrintHtmlCallback { params, tell ->

        val printer = params.printers.default!!
        val job = printer.job

        job.settings()
            .header(
                "<span style='font-size: 12px;'>Page header:</span>"
                        + "<span class='title'></span>"
            )
            .footer(
                "<span style='font-size: 12px;'>Page footer:</span>"
                        + "<span class='pageNumber'></span>"
            )
            .paperSize(PaperSize.ISO_A4)
            .colorModel(ColorModel.COLOR)
            .enablePrintingBackgrounds()
            .disablePrintingHeaderFooter()
            .orientation(Orientation.PORTRAIT)
            .apply()

        job.subscribe<PrintCompleted> { event ->
            if (event.isSuccess) {
                println("Printing is completed successfully.")
            } else {
                println("Printing has failed.")
            }
            engine.close() // Exits from `main` as the printing completes.
        }

        tell.proceed(printer)
    })

    browser.navigation.loadUrlAndWait("https://en.wikipedia.org/wiki/Printing")
    browser.mainFrame?.print()
}
