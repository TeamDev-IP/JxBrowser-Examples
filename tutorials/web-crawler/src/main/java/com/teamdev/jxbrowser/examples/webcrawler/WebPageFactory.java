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

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.frame.Frame;
import com.teamdev.jxbrowser.navigation.NavigationException;
import com.teamdev.jxbrowser.navigation.TimeoutException;
import com.teamdev.jxbrowser.net.NetError;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A factory used for creating a {@link WebPage} instance for the discovered URLs.
 */
public final class WebPageFactory {

    /**
     * The delay in milliseconds between navigation.
     *
     * <p>Some web servers detect frequent HTTP/HTTPS requests and mark this activity as
     * "suspicious" that might be a sing of a DDoS attack. Too frequent request might be aborted in
     * this case. To get rid of aborted requests, we use a delay between navigation, to simulate a
     * human. Human cannot manually navigate to a web page dozen times per second.
     *
     * <p>Politeness delay in milliseconds (delay between sending two requests to the same host).
     */
    private static final int NAVIGATION_DELAY_MS = 500;

    /**
     * The number or navigation attempts.
     *
     * <p>Some web servers might abort "suspicious" requests. In this case navigation will fail
     * with {@link NetError#ABORTED}. We do not give up and try sending request again several times
     * with bigger delay.
     */
    private static final int NAVIGATION_ATTEMPTS = 3;

    /**
     * Creates a {@link WebPage} instance for the given {@code url}.
     *
     * @param browser   the web browser instance used to load web page and access its DOM
     * @param url       the URL of the web page to load and analyze
     * @param targetUrl the URL of the target web page we start our analysis from
     *
     * @return a {@link WebPage} instance that contains the info about web page such as the anchors
     * and HTML of the web page
     */
    public WebPage create(Browser browser, String url, String targetUrl) {
        NetError status = loadUrlAndWait(browser, url, NAVIGATION_ATTEMPTS);
        if (status != NetError.OK) {
            return WebPage.newInstance(url, status);
        }
        Set<String> anchors = findAnchors(browser, targetUrl);
        String html = html(browser);
        return WebPage.newInstance(url, html, anchors);
    }

    /**
     * Loads the given {@code url} and waits until the web page is loaded completely.
     *
     * @return {@code true} if the web page has been loaded successfully. If the given URL is dead
     * or we didn't manage to load it within 45 seconds, returns {@code false}.
     *
     * @implNote before every navigation we wait for {@link #NAVIGATION_DELAY_MS} because web server
     * may abort often URL requests to protect itself from DDoS attacks.
     */
    private NetError loadUrlAndWait(Browser browser, String url, int navigationAttempts) {
        // All our attempts to load the given url were rejected (
        // We give up and continue processing other web pages.
        if (navigationAttempts == 0) {
            return NetError.ABORTED;
        }
        try {
            // Web server might abort often URL requests to protect itself from DDoS attack.
            // Use a delay between URL requests.
            TimeUnit.MILLISECONDS.sleep((long) NAVIGATION_DELAY_MS * navigationAttempts);
            // Load the given URL and wait until the web page is loaded completely.
            browser.navigation().loadUrlAndWait(url);
        } catch (NavigationException e) {
            NetError netError = e.netError();
            if (netError == NetError.ABORTED) {
                // If web server aborts our request, try again.
                return loadUrlAndWait(browser, url, --navigationAttempts);
            }
            return netError;
        } catch (TimeoutException e) {
            // Web server did not respond within `Navigation.defaultTimeoutInSeconds()`.
            return NetError.CONNECTION_TIMED_OUT;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return NetError.OK;
    }

    /**
     * Returns a stream that represents HTML of the currently loaded web page.
     */
    private String html(Browser browser) {
        AtomicReference<String> htmlRef = new AtomicReference<>("");
        browser.mainFrame().ifPresent(frame -> htmlRef.set(frame.html()));
        return htmlRef.get();
    }

    /**
     * Returns a set of the link URLs on the currently loaded web page.
     *
     * @implNote we find the links on the main frame document only. We skip {@code IFRAME}s on the
     * web page because very often they represent some third-party widgets such as Google analytics,
     * social network widgets, etc.
     */
    private Set<String> findAnchors(Browser browser, String targetUrl) {
        Set<String> result = new HashSet<>();
        browser.mainFrame().flatMap(Frame::document).ifPresent(document ->
                document.findElementsByTagName("a").forEach(element -> {
                    try {
                        String href = element.attributeValue("href");
                        normalizeUrl(href, targetUrl).ifPresent(result::add);
                    } catch (IllegalStateException ignore) {
                        // DOM of a web page might be changed dynamically from JavaScript.
                        // The DOM HTML Element we analyze, might be removed during our analysis.
                        // We do not analyze attributes of the removed DOM elements.
                    }
                }));
        return result;
    }

    private Optional<String> normalizeUrl(String url, String targetUrl) {
        if (url.isEmpty()) {
            return Optional.empty();
        }
        if (url.equals("/")) {
            return Optional.empty();
        }
        if (url.startsWith("#")) {
            return Optional.empty();
        }
        if (url.startsWith("tel:")) {
            return Optional.empty();
        }
        if (url.startsWith("mailto:")) {
            return Optional.empty();
        }
        if (url.startsWith("javascript:")) {
            return Optional.empty();
        }
        // Normalize URL.
        // If it is a relative root URL, convert to a full URL.
        if (url.startsWith("/")) {
            url = targetUrl + url;
        }
        // Remove "/" at the end of URL.
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return Optional.of(url);
    }
}
