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

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.ZoomService;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * This example demonstrates how to modify zoom level for a
 * currently loaded web page. Zoom level will be applied to
 * the currently loaded web page only. If you navigate to
 * a different domain, its zoom level will be 100% until you
 * modify it.
 */
public class ChangeZoomLevel {

    private static final double ZOOM_LEVEL = 2.0;

    public static void main(String[] args) {
        Browser browser = new Browser();
        BrowserView browserView = new BrowserView(browser);

        JFrame frame = new JFrame("JxBrowser – Zoom Level");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(browserView, BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Listen to the zoom changed events.
        BrowserContext context = browser.getContext();
        ZoomService zoomService = context.getZoomService();
        zoomService.addZoomListener(event -> {
            System.out.println("Url: " + event.getURL());
            System.out.println("Zoom level: " + event.getZoomLevel());
        });

        // Modify zoom level every time when the main frame is loaded.
        browser.addLoadListener(new LoadAdapter() {
            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent event) {
                if (event.isMainFrame()) {
                    event.getBrowser().setZoomLevel(ZOOM_LEVEL);
                }
            }
        });
        browser.loadURL("https://www.google.com");
    }
}
