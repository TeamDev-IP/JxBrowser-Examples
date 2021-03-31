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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.teamdev.jxbrowser.engine.RenderingMode.OFF_SCREEN;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.frame.Frame;
import com.teamdev.jxbrowser.navigation.NavigationException;
import com.teamdev.jxbrowser.navigation.TimeoutException;
import java.io.Closeable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * This example demonstrates how to scan a web site and find both internal and external dead links.
 */
public final class DeadLinks implements Closeable {

    /**
     * The URL of a web site we would like to analyze.
     */
    private static final String TARGET_URL = "https://jxbrowser-support.teamdev.com";

    /**
     * We might not want to analyzed some of the internal web pages, such as Javadoc API
     * Specifications in our case.
     */
    private static final Set<String> EXCLUDED_URLS = ImmutableSet.of(TARGET_URL + "/javadoc");

    /**
     * The delay in milliseconds between navigation.
     *
     * <p>Some web servers detect frequent HTTP/HTTPS requests and mark this activity as
     * "suspicious" that might be a sing of a DDoS attack. Too frequent request might be aborted in
     * this case. To get rid of aborted requests, we use a delay between navigation, to simulate a
     * human. Human cannot manually navigate to a web page dozen times per second.
     */
    private static final int NAVIGATION_DELAY_MS = 500;

    public static void main(String[] args) {
        try (DeadLinks deadLinks = new DeadLinks(TARGET_URL, EXCLUDED_URLS)) {
            reportDeadLinks(deadLinks.list());
        }
    }

    private static void reportDeadLinks(Map<String, Set<String>> links) {
        println("---");
        println("Results:");
        for (String pageUrl : links.keySet()) {
            Set<String> pageDeadLinks = links.get(pageUrl);
            if (!pageDeadLinks.isEmpty()) {
                println("\tDead or problematic links on " + pageUrl);
                pageDeadLinks.forEach(deadLink -> println("\t\t" + deadLink));
            }
        }
    }

    private static void print(String message) {
        System.out.print(message);
    }

    private static void println(String message) {
        System.out.println(message);
    }

    private final Engine engine;
    private final Browser browser;
    private final String targetUrl;
    private final Set<String> deadLinks;
    private final Set<String> excludedUrls;
    private final Map<String, Set<String>> links;

    private DeadLinks(String targetUrl, Set<String> excludedUrls) {
        checkNotNull(targetUrl);
        checkNotNull(excludedUrls);

        this.targetUrl = targetUrl;
        this.excludedUrls = ImmutableSet.copyOf(excludedUrls);
        this.deadLinks = new HashSet<>();
        this.links = new HashMap<>();

        engine = Engine.newInstance(
                EngineOptions.newBuilder(OFF_SCREEN)
                        .disableChromiumTraffic()
                        .build());
        browser = engine.newBrowser();

        // Make sure that the given target URL is valid and accessible.
        if (loadUrlAndWait(targetUrl)) {
            throw new IllegalArgumentException(
                    "The target URL is no accessible: " + targetUrl);
        }

        // Analyze the target URL and prepare the list of all links on the web site.
        analyze(targetUrl);

        // Exclude the valid links from the list, so that it contains only
        // web pages with dead links.
        links.keySet().forEach(pageUrl ->
                links.get(pageUrl).removeIf(o -> !deadLinks.contains(o)));
    }

    /**
     * Returns the list of the web pages where the dead or problematic links were found.
     */
    public Map<String, Set<String>> list() {
        return ImmutableMap.copyOf(links);
    }

    /**
     * Releases all allocated resources.
     */
    @Override
    public void close() {
        engine.close();
    }

    private void analyze(String url) {
        // If we already visited this link or the link is dead, skip it.
        if (links.containsKey(url) || deadLinks.contains(url)) {
            return;
        }

        print("Analyzing " + url + " ...");

        // Do not analyze the links that match the exclude URLs.
        for (String excludeUrl : excludedUrls) {
            if (url.startsWith(excludeUrl)) {
                println(" [SKIP]");
                return;
            }
        }

        // Try loading the URl and check if we can load it.
        if (loadUrlAndWait(url)) {
            deadLinks.add(url);
            return;
        }

        // Add the URL to the list of visited and analyzed URLs.
        links.put(url, new HashSet<>());

        println(" [OK]");

        // If the URL is alive, but it's an external URL, do not analyze it.
        // We don't analyze external URLs.
        if (!url.startsWith(targetUrl)) {
            return;
        }

        // Get the set of all links on the currently loaded web page.
        Set<String> pageLinks = linksOnMainFrame();

        // Remember all the links found on the web page.
        links.get(url).addAll(pageLinks);

        // Recursively analyze every link found on this web page.
        pageLinks.forEach(this::analyze);
    }

    /**
     * Returns a set of the link URLs on the currently loaded web page.
     *
     * @implNote we find the links on the main frame document only. We skip {@code IFRAME}s on the
     * web page because very often they represent some third-party widgets such as Google analytics,
     * social network widgets, etc.
     */
    private Set<String> linksOnMainFrame() {
        Set<String> result = new HashSet<>();
        browser.mainFrame().flatMap(Frame::document).ifPresent(document ->
                document.findElementsByTagName("a").forEach(element -> {
                    try {
                        String href = element.attributeValue("href");
                        normalizeUrl(href).ifPresent(result::add);
                    } catch (IllegalStateException ignore) {
                        // DOM of a web page might be changed dynamically from JavaScript.
                        // The DOM HTML Element we analyze, might be removed during our analysis.
                        // We do not analyze attributes of the removed DOM elements.
                    }
                }));
        return result;
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
    private boolean loadUrlAndWait(String url) {
        try {
            // We need this delay because web server abort often URL requests
            // to protect itself from DDoS attack.
            TimeUnit.MILLISECONDS.sleep(NAVIGATION_DELAY_MS);
            // Load the given URL and wait until the web page is loaded completely.
            browser.navigation().loadUrlAndWait(url);
        } catch (NavigationException | TimeoutException e) {
            println(e.getMessage());
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }

    private Optional<String> normalizeUrl(String url) {
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
