/*
 *  Copyright 2021, TeamDev. All rights reserved.
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

import com.teamdev.jxbrowser.net.NetError;
import java.util.Collections;
import java.util.Set;

public final class WebPage {

    public static WebPage newInstance(String url, NetError status) {
        return new WebPage(url, status);
    }

    public static WebPage newInstance(String url, String html, Set<String> anchors) {
        return new WebPage(url, NetError.OK, html, anchors);
    }

    private final String url;
    private final String html;
    private final Set<String> anchors;

    private final NetError status;

    private WebPage(String url, NetError status) {
        this(url, status, "", Collections.emptySet());
    }

    private WebPage(String url, NetError status, String html, Set<String> anchors) {
        this.url = url;
        this.html = html;
        this.status = status;
        this.anchors = anchors;
    }

    public String url() {
        return url;
    }

    public Set<String> anchors() {
        return anchors;
    }

    public String html() {
        return html;
    }

    public NetError status() {
        return status;
    }
}
