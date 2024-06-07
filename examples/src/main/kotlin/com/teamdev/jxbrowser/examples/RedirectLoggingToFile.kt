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

package com.teamdev.jxbrowser.examples

import com.teamdev.jxbrowser.dsl.Engine
import com.teamdev.jxbrowser.engine.RenderingMode
import com.teamdev.jxbrowser.logging.Level
import com.teamdev.jxbrowser.logging.Logger
import kotlin.io.path.Path

/**
 * This example demonstrates how to redirect all JxBrowser log messages
 * to the '*.log' file.
 */
fun main() {
    val loggingFile = Path("jxbrowser.log")

    // Use `jxbrowser.logging.file` property to redirect logging to the file.
    System.setProperty("jxbrowser.logging.file", "$loggingFile")

    // Set logging level to `ALL` to see some logs upon `Engine` creation.
    Logger.level(Level.ALL)

    val engine = Engine(RenderingMode.OFF_SCREEN)
    engine.close()
}
