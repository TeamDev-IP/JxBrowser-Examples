/*
 *  Copyright 2019, TeamDev. All rights reserved.
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

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.navigation.LoadUrlParams;
import com.teamdev.jxbrowser.net.TextData;
import com.teamdev.jxbrowser.net.callback.BeforeSendUploadDataCallback;

import static com.teamdev.jxbrowser.engine.RenderingMode.OFF_SCREEN;

/**
 * This example demonstrates how to override POST upload data before it is sent to the server.
 */
public final class OverrideUploadData {

    public static void main(String[] args) {
        Engine engine = Engine.newInstance(EngineOptions.newBuilder(OFF_SCREEN).build());
        Browser browser = engine.newBrowser();

        engine.network().set(BeforeSendUploadDataCallback.class, params -> {
            if ("POST".equals(params.urlRequest().method())) {
                return BeforeSendUploadDataCallback.Response.override(TextData.of("<text-data>"));
            }
            return BeforeSendUploadDataCallback.Response.proceed();
        });

        browser.navigation().loadUrl(LoadUrlParams.newBuilder("http://localhost/")
                .postData("key=value")
                .build());
    }
}
