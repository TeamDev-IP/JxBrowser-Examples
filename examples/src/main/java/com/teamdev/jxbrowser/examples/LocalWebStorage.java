/*
 *  Copyright 2022, TeamDev. All rights reserved.
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
import static java.lang.String.format;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;

/**
 * This example demonstrates how to access the local storage and perform operations with it.
 *
 * <p>Accessing the session storage is performed by calling {@code Frame.sessionStorage()} instead
 * of {@code Frame.localStorage()}
 */
public final class LocalWebStorage {

    private static final String KEY = "Name";

    public static void main(String[] args) {
        Engine engine = Engine.newInstance(OFF_SCREEN);
        Browser browser = engine.newBrowser();
        browser.navigation().loadUrlAndWait("https://www.google.com");
        browser.mainFrame().ifPresent(frame -> {
            frame.localStorage().putItem(KEY, "Tom");
            System.out.println((String) frame.executeJavaScript(
                    format("window.localStorage.getItem(\"%s\")", KEY)));
        });
    }
}
