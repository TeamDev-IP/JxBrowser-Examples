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

package com.teamdev.jxbrowser.examples.webcrawler;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.teamdev.jxbrowser.engine.RenderingMode.OFF_SCREEN;

import com.google.common.collect.ImmutableSet;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import java.io.Closeable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * A web crawler implementation that is based on JxBrowser that allows
 * discovering and analyzing the web pages, accessing their DOM and HTML
 * content, finding the broken links on a web page, etc.
 */
public final class WebCrawler implements Closeable {

    /**
     * Creates a new {@code com.teamdev.jxbrowser.examples.webcrawler.WebCrawler}
     * instance for the given target {@code url}.
     *
     * @param url            the URL of the target web page the crawler will
     *                       start its analysis
     * @param webPageFactory the factory used to create a {@link WebPage}
     *                       instance for the internal and external URLs
     */
    public static WebCrawler newInstance(String url,
            WebPageFactory webPageFactory) {
        return new WebCrawler(url, webPageFactory);
    }

    private final Engine engine;
    private final Browser browser;
    private final String targetUrl;
    private final Set<WebPage> pages;
    private final WebPageFactory pageFactory;

    private WebCrawler(String url, WebPageFactory webPageFactory) {
        checkNotNull(url);
        checkNotNull(webPageFactory);

        targetUrl = url;
        pageFactory = webPageFactory;
        pages = new HashSet<>();

        engine = Engine.newInstance(
                EngineOptions.newBuilder(OFF_SCREEN)
                        // Visit the web pages in the Chromium's incognito mode.
                        .enableIncognito()
                        .build());
        browser = engine.newBrowser();
    }

    /**
     * Starts the web crawler and reports the progress via the given {@code
     * listener}. The time required to analyze website depends on the number of
     * discovered web pages.
     *
     * <p>This operation blocks the current thread execution until the crawler
     * stops analyzing the discovered web pages.
     *
     * @param listener a listener that will be invoked to report the progress
     */
    public void start(WebCrawlerListener listener) {
        checkNotNull(listener);
        analyze(targetUrl, pageFactory, listener);
    }

    private void analyze(String url, WebPageFactory factory,
            WebCrawlerListener listener) {
        if (!isVisited(url)) {
            WebPage webPage = factory.create(browser, url);
            pages.add(webPage);

            // Notify the listener that a web page has been visited.
            listener.webPageVisited(webPage);

            // If it is an external web page, do not go through its links.
            if (url.startsWith(targetUrl)) {
                webPage.links().forEach(
                        link -> analyze(link.url(), factory, listener));
            }
        }
    }

    /**
     * Checks if the given {@code url} belongs to already visited web page.
     */
    private boolean isVisited(String url) {
        checkNotNull(url);
        return page(url).orElse(null) != null;
    }

    /**
     * Returns an immutable set of the web pages analyzed by this crawler.
     */
    public ImmutableSet<WebPage> pages() {
        return ImmutableSet.copyOf(pages);
    }

    /**
     * Returns an optional that contains a web page associated with the given
     * {@code url} or an empty options if there is no such web page.
     */
    public Optional<WebPage> page(String url) {
        checkNotNull(url);

        for (WebPage page : pages) {
            if (page.url().equals(url)) {
                return Optional.of(page);
            }
        }
        return Optional.empty();
    }

    /**
     * Releases all allocated resources and closes the web browser instance used
     * to discover and analyze the web pages.
     */
    @Override
    public void close() {
        engine.close();
    }
}
