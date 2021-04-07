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

package com.teamdev.jxbrowser.examples.webcrawler;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import com.teamdev.jxbrowser.net.NetError;
import java.util.Collections;
import java.util.Set;

/**
 * Represents the details of a web page such as its URL, HTML, the status code
 * from web server, and the list of anchors found on it.
 */
public final class WebPage {

    private final String url;
    private final String html;
    private final NetError status;
    private final ImmutableSet<Link> links;

    private WebPage(String url, NetError status) {
        this(url, status, "", Collections.emptySet());
    }

    private WebPage(String url, NetError status, String html, Set<Link> links) {
        checkNotNull(url);
        checkNotNull(status);
        checkNotNull(html);
        checkNotNull(links);

        this.url = url;
        this.html = html;
        this.status = status;
        this.links = ImmutableSet.copyOf(links);
    }

    /**
     * Creates a instance of {@code WebPage} with the given {@code url} and
     * {@code status} code.
     *
     * <p>This factory method is used to create an instance of an inaccessible
     * {@code WebPage}. The status code should represent the network error
     * obtained from web server that explains why the web page is not
     * accessible.
     */
    static WebPage newInstance(String url, NetError status) {
        return new WebPage(url, status);
    }

    /**
     * Creates a {@code WebPage} instance with the given details.
     *
     * @param url   the URL of the web page
     * @param html  the HTML content of the web page
     * @param links the list of links found on the web page
     */
    static WebPage newInstance(String url, String html, Set<Link> links) {
        return new WebPage(url, NetError.OK, html, links);
    }

    /**
     * Returns URL of the web page.
     */
    public String url() {
        return url;
    }

    /**
     * Returns an immutable set of the links on this web page.
     */
    public ImmutableSet<Link> links() {
        return links;
    }

    /**
     * Returns HTML of this web page.
     */
    public String html() {
        return html;
    }

    /**
     * Returns the status code from web server.
     */
    public NetError status() {
        return status;
    }
}
