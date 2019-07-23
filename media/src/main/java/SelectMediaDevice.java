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

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.media.MediaDevice;
import com.teamdev.jxbrowser.media.callback.SelectMediaDeviceCallback;

import java.net.URL;

import static com.teamdev.jxbrowser.engine.RenderingMode.OFF_SCREEN;
import static com.teamdev.jxbrowser.media.MediaDeviceType.AUDIO_DEVICE;
import static com.teamdev.jxbrowser.media.callback.SelectMediaDeviceCallback.Response;

/**
 * This example demonstrates how to programmatically select a media device when JavaScript requests
 * it.
 */
public final class SelectMediaDevice {
    public static void main(String[] args) {
        Engine engine = Engine.newInstance(
                EngineOptions.newBuilder(OFF_SCREEN).build());
        Browser browser = engine.newBrowser();

        engine.mediaDevices().set(SelectMediaDeviceCallback.class, params -> {
            System.out.println(params.mediaDeviceType() == AUDIO_DEVICE ?
                    "Selecting audio device..." : "Selecting video device...");

            MediaDevice device = params.mediaDevices().get(0);
            System.out.println("Selected device:\n" + device);
            return Response.select(device);
        });

        URL url = SelectMediaDevice.class.getResource("media-devices-callback.html");
        browser.navigation().loadUrlAndWait(url.toString());
        browser.mainFrame().ifPresent(frame ->
                frame.executeJavaScript("requestAudio(); requestVideo();"));
    }
}
