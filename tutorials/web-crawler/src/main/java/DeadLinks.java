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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This example demonstrates how analyze the given web page using {@link WebCrawler}, find all the
 * links to internal and external web pages, recursively go through all internal web pages, find
 * anchors and get HTML for each web page.
 *
 * <p>Then go through all the discovered web pages and print the web pages with the problematic or
 * dead links.
 */
public final class DeadLinks {

    /**
     * A URL of the target web page to analyze.
     */
    private static final String URL = "https://teamdev.com/jxbrowser";

    public static void main(String[] args) {
        try (WebCrawler crawler = WebCrawler.newInstance(URL, new WebPageFactory())) {
            // Start web crawler and print the details of each discovered and analyzed web page.
            crawler.start(DeadLinks::print);
            // Collect the web pages with problematic or dead links and print them.
            print(collectProblematicWebPages(crawler));
        }
    }

    private static Map<WebPage, Set<DeadLink>> collectProblematicWebPages(WebCrawler crawler) {
        Map<WebPage, Set<DeadLink>> result = new HashMap<>();
        crawler.pages().forEach(page -> {
            Set<DeadLink> deadLinks = new HashSet<>();
            page.anchors().forEach(anchor -> crawler.page(anchor).ifPresent(webPage -> {
                if (webPage.status() != NetError.OK) {
                    deadLinks.add(DeadLink.of(webPage.url(), webPage.status()));
                }
            }));
            if (!deadLinks.isEmpty()) {
                result.put(page, deadLinks);
            }
        });
        return result;
    }

    private static void print(WebPage webPage) {
        System.out.println(webPage.url() + " [" + webPage.status() + "]");
    }

    private static void print(Map<WebPage, Set<DeadLink>> webPages) {
        System.out.println("Dead or problematic links:");
        StringBuilder builder = new StringBuilder();
        for (WebPage webPage : webPages.keySet()) {
            builder.append(webPage.url());
            Set<DeadLink> deadLinks = webPages.get(webPage);
            for (DeadLink deadLink : deadLinks) {
                builder.append("\t");
                builder.append(deadLink.url());
                builder.append(" ");
                builder.append(deadLink.netError());
            }
        }
        System.out.println(builder);
    }
}
