/*
 *  Copyright 2026, TeamDev. All rights reserved.
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

import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.navigation.LoadUrlParams;
import com.teamdev.jxbrowser.net.FormData;
import com.teamdev.jxbrowser.net.FormData.Pair;
import com.teamdev.jxbrowser.net.TextData;
import com.teamdev.jxbrowser.net.callback.BeforeSendUploadDataCallback;

/**
 * This example demonstrates how to override the POST payload before
 * it is sent to the server.
 */
public final class UploadData {

    public static void main(String[] args) {
        try (var engine = Engine.newInstance(OFF_SCREEN)) {
            engine.network().set(BeforeSendUploadDataCallback.class, params -> {
                if ("POST".equals(params.urlRequest().method())) {
                    // Override the original POST data with a text data.
                    return BeforeSendUploadDataCallback.Response
                            .override(TextData.of("<text-data>"));
                }
                return BeforeSendUploadDataCallback.Response.proceed();
            });
            var browser = engine.newBrowser();

            // Load URL request using POST method and send form data, that
            // is going to be overridden.
            var formData = FormData.newBuilder()
                    .addPair(Pair.of("key", "value"))
                    .build();
            browser.navigation().loadUrlAndWait(
                    LoadUrlParams.newBuilder("http://localhost/")
                            .uploadData(formData)
                            .build());
        }
    }
}
