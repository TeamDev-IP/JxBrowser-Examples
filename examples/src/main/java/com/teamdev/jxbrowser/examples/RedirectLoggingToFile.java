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

import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.logging.Level;
import com.teamdev.jxbrowser.logging.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This example demonstrates how to redirect all JxBrowser log messages to the '*.log' file.
 *
 * <p>The "jxbrowser-logs..." directory is created in the user temp directory and is not deleted.
 */
public final class RedirectLoggingToFile {

    public static void main(String[] args) {
        Path loggingDir;
        try {
            loggingDir = Files.createTempDirectory("jxbrowser-logs");
        } catch (IOException e) {
            throw new RuntimeException("Failed to create a temporary directory", e);
        }
        Path loggingFile = loggingDir.resolve("jxbrowser.log");
        System.setProperty("jxbrowser.logging.file",
                loggingFile.toAbsolutePath().toString());

        // Set logging level to `ALL` to see some logs upon `Engine` creation.
        Logger.level(Level.ALL);

        System.out.println("Log file path: " + loggingFile.toAbsolutePath());

        Engine engine = Engine.newInstance(HARDWARE_ACCELERATED);
        engine.close();
    }
}
